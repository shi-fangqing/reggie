package com.shi.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shi.reggie.common.BaseContext;
import com.shi.reggie.common.R;
import com.shi.reggie.entity.Orders;
import com.shi.reggie.service.OrdersService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrdersController {

    @Resource
    private OrdersService ordersService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        ordersService.submitOrder(orders);
        return R.success("支付成功");
    }

    @GetMapping("/userPage")
    public R<Page<Orders>> listOrder(@RequestParam Integer page,@RequestParam Integer pageSize){
        Page<Orders> ordersPage = ordersService.page(new Page<>(page, pageSize));
        return R.success(ordersPage);
    }

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
    @PutMapping
    public R<String> updateStatus(@RequestBody   Orders orders){
        ordersService.updateById(orders);
        return R.success("更新成功");
    }
}
