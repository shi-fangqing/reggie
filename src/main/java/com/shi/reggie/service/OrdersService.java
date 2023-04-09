package com.shi.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shi.reggie.entity.Orders;

public interface OrdersService extends IService<Orders> {

    void submitOrder(Orders orders);
}
