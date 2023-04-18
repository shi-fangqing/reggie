package com.shi.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.shi.reggie.common.BaseContext;
import com.shi.reggie.common.R;
import com.shi.reggie.entity.ShoppingCart;
import com.shi.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Resource
    private ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        //查询当前用户的 购物车信息
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(new LambdaQueryWrapper<ShoppingCart>()
                                                                    .eq(ShoppingCart::getUserId,BaseContext.getCurrentId()));
        return R.success(shoppingCartList);
    }

    @PostMapping("/add")
    public R<String> save(@RequestBody ShoppingCart shoppingCart){
        //如果 userId、dishId 相同，则为同一条数据
        //如果 userId、setmealId 相同，则为同一条数据
        shoppingCart.setUserId(BaseContext.getCurrentId());
        ShoppingCart cart = shoppingCartService.getOne(new LambdaQueryWrapper<ShoppingCart>()
                .eq(shoppingCart.getUserId() != null, ShoppingCart::getUserId, shoppingCart.getUserId())
                .eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId())
                .eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId()));
        //已有该菜品或套餐
        if(cart!=null){
            cart.setNumber(cart.getNumber()+1);
            shoppingCartService.updateById(cart);
            return R.success("添加成功");
        }
        //添加新的菜品或套餐
        shoppingCart.setNumber(1);
        shoppingCartService.save(shoppingCart);
        return R.success("添加成功");
    }

    @PostMapping("/sub")
    public R<String> remove(@RequestBody ShoppingCart shoppingCart){
        //查询已存在的 菜品或套餐
        ShoppingCart cart = shoppingCartService.getOne(new LambdaQueryWrapper<ShoppingCart>()
                .eq(ShoppingCart::getUserId, BaseContext.getCurrentId())
                .eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId())
                .eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId()));

        //如果当前 number==1，则直接移除
        if(cart.getNumber()==1){
            shoppingCartService.removeById(cart);
            return R.success("删除成功");
        }
        //移除一次 number-1
        boolean cart1 = shoppingCartService.update(new LambdaUpdateWrapper<ShoppingCart>()
                .set(ShoppingCart::getNumber, cart.getNumber() - 1)
                .eq(ShoppingCart::getUserId, BaseContext.getCurrentId())
                .eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId())
                .eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId()));

        return R.success("删除成功");
    }

    @DeleteMapping("/clean")
    public R<String> clear(){
        //清空当前用户的购物车
        shoppingCartService.remove(new LambdaQueryWrapper<ShoppingCart>()
                                    .eq(ShoppingCart::getUserId,BaseContext.getCurrentId()));
        return R.success("已清空");
    }
}
