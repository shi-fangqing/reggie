package com.shi.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shi.reggie.common.BaseContext;
import com.shi.reggie.entity.*;
import com.shi.reggie.exception.CustomException;
import com.shi.reggie.mapper.OrdersMapper;
import com.shi.reggie.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Resource
    private UserService userService;

    @Resource
    private AddressBookService addressBookService;

    @Resource
    private ShoppingCartService shoppingCartService;

    @Resource
    private OrderDetailService orderDetailService;

    @Transactional
    @Override
    public void submitOrder(Orders orders) {
        //查询该用户信息
        User user = userService.getById(BaseContext.getCurrentId());
        //查询地址信息
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if (addressBook==null){
            throw new CustomException("没有设置默认收货地址");
        }
        //查询订单中的菜品和套餐 价格总和
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(new LambdaQueryWrapper<ShoppingCart>()
                .eq(ShoppingCart::getUserId, BaseContext.getCurrentId()));
        if(shoppingCartList==null){
            throw new CustomException("购物车为空");
        }
        double amountSum=0;
        for(ShoppingCart shoppingCart:shoppingCartList){
            Integer number = shoppingCart.getNumber();
            Double amount = shoppingCart.getAmount();
            amountSum+=number*amount;
        }
        //1. 填充orders表信息， 计算订单总金额
        orders.setNumber(IdWorker.getIdStr());
        orders.setStatus(2);
        orders.setUserId(BaseContext.getCurrentId());
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setAmount(amountSum);
        orders.setPhone(user.getPhone());
        orders.setAddress((addressBook.getProvinceName()==null?"":addressBook.getProvinceName())
                        + (addressBook.getCityName()==null?"":addressBook.getCityName())
                        + (addressBook.getDistrictName()==null?"":addressBook.getDistrictName())
                        + (addressBook.getDetail()==null?"":addressBook.getDetail()));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        this.save(orders);
        //2. 将购物车内的菜品和套餐，填充到order_detail 表信息
        List<OrderDetail> orderDetailList = shoppingCartList.stream().map(shoppingCart -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setName(shoppingCart.getName());
            orderDetail.setImage(shoppingCart.getImage());
            orderDetail.setOrderId(orders.getId());
            orderDetail.setDishId(shoppingCart.getDishId());
            orderDetail.setSetmealId(shoppingCart.getSetmealId());
            orderDetail.setDishFlavor(shoppingCart.getDishFlavor());
            orderDetail.setNumber(shoppingCart.getNumber());
            orderDetail.setAmount(shoppingCart.getAmount());
            return orderDetail;
        }).collect(Collectors.toList());
        orderDetailService.saveBatch(orderDetailList);
        //3. 清空该用户购物车信息
        shoppingCartService.remove(new LambdaQueryWrapper<ShoppingCart>()
                                        .eq(ShoppingCart::getUserId,BaseContext.getCurrentId()));
    }
}
