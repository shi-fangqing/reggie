package com.shi.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shi.reggie.dto.DishDto;
import com.shi.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    DishDto getDishWithFlavorById(Long id);

    void saveDishWithFlavor(DishDto dishDto);

    void updateDishWithFlavor(DishDto dishDto);

    void removeDishWithFlavor(List<Long> ids);
}
