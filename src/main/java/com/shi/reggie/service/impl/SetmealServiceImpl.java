package com.shi.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shi.reggie.dto.SetmealDto;
import com.shi.reggie.entity.Category;
import com.shi.reggie.entity.Setmeal;
import com.shi.reggie.entity.SetmealDish;
import com.shi.reggie.mapper.SetmealMapper;
import com.shi.reggie.service.CategoryService;
import com.shi.reggie.service.SetmealDishService;
import com.shi.reggie.service.SetmealService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Resource
    private SetmealDishService setmealDishService;

    @Resource
    private CategoryService categoryService;

    @Transactional
    @Override
    public void saveSetmealWithDish(SetmealDto setmealDto) {
        //1. 保存套餐的信息，获得自动生成的套餐id
        this.save(setmealDto);
        //2. 保存套餐内菜品的信息，填充套餐id
        Long setmealId = setmealDto.getId();
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });
        setmealDishService.saveBatch(setmealDishes);
    }

    @Transactional
    @Override
    public void removeSetmealWithDish(List<Long> ids) {

        //1. 删除套餐对应的菜品
        setmealDishService.remove(new LambdaQueryWrapper<SetmealDish>()
                .in(SetmealDish::getSetmealId,ids));
        //2. 删除套餐
        this.removeBatchByIds(ids);
    }

    @Override
    public SetmealDto getSetmealWithDishById(Long id) {
        //1. 通过 套餐id 获取套餐的基本信息
        Setmeal setmeal = this.getById(id);
        //2. 通过 套餐的categoryId 获得categoryName
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper=new LambdaQueryWrapper<>();
        categoryLambdaQueryWrapper.eq(Category::getId,setmeal.getCategoryId());
        Category category = categoryService.getOne(categoryLambdaQueryWrapper);
        //3. 完成属性的拷贝 setmeal-->setmealDto
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        //4. 通过 套餐id 获取所有的菜品
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> setmealDishList = setmealDishService.list(setmealDishLambdaQueryWrapper);
        //5. 将所有的菜品 封装到 setmealDto的 setmealDishes集合中
        setmealDto.setSetmealDishes(setmealDishList);
        //6. 封装categoryName到 setmealDto
        setmealDto.setCategoryName(category.getName());
        return setmealDto;
    }

    @Transactional
    @Override
    public void updateSetmealWithDish(SetmealDto setmealDto) {
        //1. 通过 套餐id 删除对应的所有菜品
        setmealDishService.remove(new LambdaQueryWrapper<SetmealDish>()
                        .eq(SetmealDish::getSetmealId,setmealDto.getId()));
        //2. 为所有菜品添加 套餐id
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        List<SetmealDish> newSetmealDishList = setmealDishes.stream().map(setmealDish -> {
            setmealDish.setSetmealId(setmealDto.getId());
            return setmealDish;
        }).collect(Collectors.toList());
        //3. 重新添加新的菜品到 setmeal_dish
        setmealDishService.saveBatch(newSetmealDishList);
        //4. 更新基本套餐信息
        this.updateById(setmealDto);
    }
}
