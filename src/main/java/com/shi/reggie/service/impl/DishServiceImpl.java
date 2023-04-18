package com.shi.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shi.reggie.dto.DishDto;
import com.shi.reggie.entity.Category;
import com.shi.reggie.entity.Dish;
import com.shi.reggie.entity.DishFlavor;
import com.shi.reggie.mapper.DishMapper;
import com.shi.reggie.service.CategoryService;
import com.shi.reggie.service.DishFlavorService;
import com.shi.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Resource
    private DishFlavorService dishFlavorService;

    @Resource
    private CategoryService categoryService;


    @Override
    public DishDto getDishWithFlavorById(Long id) {
        //通过菜品id 获取菜品信息，口味偏好，菜品类型，然后封装到dishDto对象
        Dish dish = this.getById(id);
        Long categoryId = dish.getCategoryId();
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper=new LambdaQueryWrapper<>();
        categoryLambdaQueryWrapper.eq(Category::getId,categoryId);
        Category category = categoryService.getOne(categoryLambdaQueryWrapper);
        //封装口味的查询条件
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper=new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
        //数据copy
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        dishDto.setCategoryName(category.getName());
        dishDto.setFlavors(dishFlavorList);
        return dishDto;
    }

    @Transactional
    @Override
    public void saveDishWithFlavor(DishDto dishDto) {
        //保存菜品信息，获得生成的 菜品id
        //保存菜品
        this.save(dishDto);
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.forEach(flavor->{
            flavor.setDishId(dishId);
        });
        //保存口味
        dishFlavorService.saveBatch(flavors);
    }

    @Transactional
    @Override
    public void updateDishWithFlavor(DishDto dishDto) {
        //1. 删除原有口味
        dishFlavorService.remove(new LambdaQueryWrapper<DishFlavor>()
                        .eq(DishFlavor::getDishId,dishDto.getId()));
        //2. 添加新的口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        List<DishFlavor> newFlavorList = flavors.stream().map(flavor -> {
            flavor.setDishId(dishDto.getId());
            return flavor;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(newFlavorList);
        //3. 修改菜品基本信息
        this.updateById(dishDto);
    }


    @Transactional
    @Override
    public void removeDishWithFlavor(List<Long> ids) {
        //1. 删除菜品对应的口味
        dishFlavorService.remove(new LambdaQueryWrapper<DishFlavor>()
                .in(DishFlavor::getDishId,ids));
        //2. 删除菜品
        this.removeByIds(ids);
    }
}
