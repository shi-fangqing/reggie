package com.shi.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shi.reggie.common.R;
import com.shi.reggie.dto.SetmealDto;
import com.shi.reggie.entity.Category;
import com.shi.reggie.entity.Dish;
import com.shi.reggie.entity.Setmeal;
import com.shi.reggie.entity.SetmealDish;
import com.shi.reggie.service.CategoryService;
import com.shi.reggie.service.DishService;
import com.shi.reggie.service.SetmealDishService;
import com.shi.reggie.service.SetmealService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {

    @Resource
    private SetmealService setmealService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private DishService dishService;

    @Resource
    private SetmealDishService setmealDishService;
    /**
     * 分页查询套餐信息
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> getPage(@RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam(required = false) String name){
        log.info("套餐分页信息："+"page="+page+"\tpageSize="+pageSize+"\tname="+name);
        //分页查询 setmeal
        Page<Setmeal> setmealPage = setmealService.page(new Page<>(page, pageSize), new LambdaQueryWrapper<Setmeal>()
                .like(StringUtils.hasLength(name), Setmeal::getName, name));

        //将setmealPage的基本信息 拷贝到 setmealDtoPage，忽略 records
        Page<SetmealDto> setmealDtoPage=new Page<>();
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");

        //将categoryName属性 封装到每一个setmealDto
        List<SetmealDto> setmealDtoList = setmealPage.getRecords().stream().map(setmeal -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal, setmealDto);
            Category category = categoryService.getById(setmeal.getCategoryId());
            setmealDto.setCategoryName(category.getName());
            return setmealDto;
        }).collect(Collectors.toList());

        //修改页面的 records属性
        setmealDtoPage.setRecords(setmealDtoList);
        return R.success(setmealDtoPage);
    }

    /**
     * 查询套餐的基本信息，不包含内置菜品
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> listSetmealDtoByCategoryId(Setmeal setmeal){
        // 查询 setmealList
        List<Setmeal> setmealList = setmealService.list(new LambdaQueryWrapper<Setmeal>()
                .eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId())
                .eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus()));
        return R.success(setmealList);
    }

    @GetMapping("/dish/{id}")
    public R<List<SetmealDish>> listSetmealDishBySetmealId(@PathVariable Long id){
        List<SetmealDish> setmealDishList = setmealDishService.list(new LambdaQueryWrapper<SetmealDish>()
                .eq(id!=null,SetmealDish::getSetmealId, id));
        return R.success(setmealDishList);
    }
    /**
     * 通过套餐id 获得套餐的信息 用于页面数据的回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getSetmealDto(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getSetmealWithDishById(id);
        return R.success(setmealDto);
    }

    /**
     * 修改套餐
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> updateSetmeal(@RequestBody SetmealDto setmealDto){
        log.info("setmealDto:\t"+setmealDto);
        setmealService.updateSetmealWithDish(setmealDto);
        return R.success("修改成功");
    }
    /**
     * 添加套餐
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("SetmealDto:\t"+setmealDto);
        setmealService.saveSetmealWithDish(setmealDto);
        return R.success("保存成功");
    }

    /**
     * 更新套餐的售卖状态
     * 可单个修改，也可批量修改
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateSetmealStatus(@PathVariable Integer status,@RequestParam(required = false) List<Long> ids){
        //批量更新status
        setmealService.update(new LambdaUpdateWrapper<Setmeal>()
                .set(Setmeal::getStatus,status)
                .in(Setmeal::getId,ids));
        if(status==0){
            return R.success("停售成功");
        }
        return R.success("启售成功");
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> remove(@RequestParam List<Long> ids){
        setmealService.removeSetmealWithDish(ids);
        return R.success("删除成功");
    }
}
