package com.shi.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shi.reggie.common.BaseContext;
import com.shi.reggie.common.R;
import com.shi.reggie.entity.Orders;
import com.shi.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;


@RestController
@Slf4j
@RequestMapping("/order")
public class OrdersController {

    @Resource
    private OrdersService ordersService;


    @CacheEvict(value = "order",allEntries = true)
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        ordersService.submitOrder(orders);
        return R.success("支付成功");
    }

    /**
     * 供用户端使用
     * 查看订单的历史记录
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page<Orders>> listOrder(@RequestParam Integer page,@RequestParam Integer pageSize){
        Page<Orders> ordersPage = ordersService.page(new Page<>(page, pageSize),new LambdaQueryWrapper<Orders>()
                                                        .eq(Orders::getUserId,BaseContext.getCurrentId()));
        return R.success(ordersPage);
    }

    /**
     * 供后端管理系统使用
     * 分页查询订单
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @Cacheable(value = "order",key = "'page:'+#page+'_'+#pageSize+'_'+#number+'_'+#beginTime+'_'+#endTime")
    @GetMapping("/page")
    public R<Page<Orders>> getPage(@RequestParam Integer page,@RequestParam Integer pageSize,
                           @RequestParam(required = false) String number,
                           @RequestParam(required = false) String beginTime,
                           @RequestParam(required = false) String endTime){
        Page<Orders> ordersPage = ordersService.page(new Page<>(page, pageSize),
                new LambdaQueryWrapper<Orders>()
                        .eq(number != null, Orders::getNumber, number)
                        .ge(beginTime != null, Orders::getCheckoutTime, beginTime)
                        .le(endTime != null, Orders::getCheckoutTime, endTime));
        return R.success(ordersPage);
    }

    /**
     * 更新订单状态
     * @param orders
     * @return
     */
    @CacheEvict(value = "order",allEntries = true)
    @PutMapping
    public R<String> updateStatus(@RequestBody Orders orders){
        ordersService.updateById(orders);
        return R.success("更新成功");
    }
}
