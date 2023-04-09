package com.shi.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.shi.reggie.common.R;
import com.shi.reggie.entity.AddressBook;
import com.shi.reggie.entity.User;
import com.shi.reggie.service.AddressBookService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookController {

    @Resource
    private AddressBookService addressBookService;

    @GetMapping("/list")
    public R<List<AddressBook>> listAddress(){
        List<AddressBook> addressBookList = addressBookService.list();
        return R.success(addressBookList);
    }

    /**
     * 根据 id， 设置默认的收货地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public R<String> updateDefaultAddress(@RequestBody AddressBook addressBook){
        //将所有的地址默认状态修改为 0
        addressBookService.update(new LambdaUpdateWrapper<AddressBook>()
                            .set(AddressBook::getIsDefault,0));
        //重新设置新的默认地址
        addressBookService.update(new LambdaUpdateWrapper<AddressBook>()
                            .set(AddressBook::getIsDefault,1)
                            .eq(AddressBook::getId,addressBook.getId()));
        return R.success("默认地址修改成功");
    }

    /**
     * 通过 收获地址id 返回一个收获地址对象，用于编辑的回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> getAddressBookById(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        return R.success(addressBook);
    }

    /**
     * 获得默认收货地址
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getDefaultAddress(){
        AddressBook addressBook = addressBookService.getOne(new LambdaQueryWrapper<AddressBook>()
                .eq(AddressBook::getIsDefault, 1));
        return R.success(addressBook);
    }

    /**
     * 更新 收货地址信息
     * @param addressBook
     * @return
     */
    @PutMapping
    public R<String> editAddressBook(@RequestBody AddressBook addressBook){
        addressBookService.updateById(addressBook);
        return R.success("更新成功");
    }

    /**
     * 通过收货地址id 删除 收货地址
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> removeAddressBookById(@RequestParam Long ids){
        addressBookService.removeById(ids);
        return R.success("删除成功");
    }

    /**
     * 添加一个收货地址
     * @param addressBook
     * @param session
     * @return
     */
    @PostMapping
    public R<String> saveAddressBook(@RequestBody AddressBook addressBook, HttpSession session){
        //获取当前 user_id, 存入addressBook
        Long userId = (Long) session.getAttribute("user");
        addressBook.setUserId(userId);
        addressBookService.save(addressBook);
        return R.success("添加成功");
    }
}
