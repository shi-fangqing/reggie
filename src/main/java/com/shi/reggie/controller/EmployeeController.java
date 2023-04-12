package com.shi.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shi.reggie.common.R;
import com.shi.reggie.entity.Employee;
import com.shi.reggie.service.EmployeeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Resource
    private EmployeeService employeeService;


    /**
     * 员工登录
     * @param session
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpSession session, @RequestBody Employee employee){

        //1. 将页面提交的密码进行md5编码
        String password = employee.getPassword();
        password=DigestUtils.md5DigestAsHex(password.getBytes());
        //2. 根据用户名查询数据库，返回null则登录失败
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        if(emp==null) return R.error("用户不存在");
        //3. 比对密码，不相同则登陆失败
        if(!emp.getPassword().equals(password)) return R.error("密码错误");
        //4. 是否被禁用
        if(emp.getStatus()==0) return R.error("ip被禁用");
        //5. 登陆成功，将用户传入session
        session.setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 退出系统
     * @param session
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpSession session){
        session.removeAttribute("employee");
        session.invalidate();
        return R.success("退出成功");
    }

    /**
     * 添加员工
     * @param employee
     * @param session
     * @return
     */
    @CacheEvict(value = "employee",allEntries = true)
    @PostMapping
    public R<String> save(@RequestBody Employee employee,HttpSession session){
        log.info("添加员工");
        employeeService.save(employee);
        return R.success("保存成功");
    }

    /**
     * 分页查询员工信息
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Cacheable(value = "employee",key = "'page:'+#page+'_'+#pageSize+'_'+#name")
    @GetMapping("/page")
    public R<Page> getPage(@RequestParam Integer page,@RequestParam Integer pageSize, @RequestParam(required = false) String name){
        //构造分页器
        Page<Employee> pageInfo = new Page<>(page,pageSize);
        //构造查询条件
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasLength(name),Employee::getName,name)
                    .orderByDesc(Employee::getCreateTime);
        //分页查询数据，查到的数据封装到pageInfo
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 修改员工账户的状态
     * @param employee
     * @return
     */
    @CacheEvict(value = "employee",allEntries = true)
    @PutMapping
    public R<String> update(HttpSession session,@RequestBody Employee employee){
        //数据库更新员工信息
        log.info("更新员工信息");
        employeeService.updateById(employee);
        return R.success("更新成功");
    }

    /**
     * 通过id查询员工信息
     * @param id
     * @return
     */
    @Cacheable(value = "employee",key = "'empId:'+#id")
    @GetMapping("/{id}")
    public R<Employee> getEmployee(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if(employee!=null) {
            return R.success(employee);
        }
        return R.error("没有该用户");
    }


}
