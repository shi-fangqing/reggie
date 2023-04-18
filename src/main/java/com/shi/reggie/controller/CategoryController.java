package com.shi.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shi.reggie.common.R;
import com.shi.reggie.entity.Category;
import com.shi.reggie.entity.Dish;
import com.shi.reggie.entity.Setmeal;
import com.shi.reggie.exception.CustomException;
import com.shi.reggie.service.CategoryService;
import com.shi.reggie.service.DishService;
import com.shi.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {
    @Resource
    private CategoryService categoryService;

    @Resource
    private DishService dishService;

    @Resource
    private SetmealService setmealService;
    /**
     * 添加菜品分类和套餐分类
     * @param category
     * @return
     */
    @CacheEvict(value = "category",allEntries = true)
    @PostMapping
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("添加成功");
    }

    /**
     * 分页查询菜品和套餐的分类信息
     * @param page
     * @param pageSize
     * @return
     */
    @Cacheable(value = "category",key = "'page:'+#page+'_'+#pageSize")
    @GetMapping("/page")
    public R<Page<Category>> getPage(@RequestParam Integer page,@RequestParam Integer pageSize){
        //封装分页信息
        Page<Category> pageInfo = new Page<>(page,pageSize);
        //封装查询条件
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        //查询
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 修改菜品或套餐分类信息
     * @param category
     * @return
     */
    @CacheEvict(value = "category",allEntries = true)
    @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    /**
     * 删除菜品或套餐的分类信息
     * @param ids
     * @return
     */
    @CacheEvict(value = "category",allEntries = true)
    @DeleteMapping
    public R<String> remove(@RequestParam Long ids){
        //查询该类别是否有菜品，如果有抛出异常
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId,ids);
        long count = dishService.count(queryWrapper);
        if(count>0) throw new CustomException("该类别有菜品存在，不能删除");
        //查询该类别是否有套餐，如果有抛出异常
        LambdaQueryWrapper<Setmeal> queryWrapper1=new LambdaQueryWrapper<>();
        queryWrapper1.eq(Setmeal::getCategoryId,ids);
        long count1 = setmealService.count(queryWrapper1);
        if (count1>0) throw new CustomException("该类别有套餐存在，不能删除");
        //可以删除
        categoryService.removeById(ids);
        return R.success("删除成功");
    }

    /**
     * 通过类型 查询菜品或套餐的分类情况
     * 为下拉框提供选项
     * @param category
      * @return
     */
    @Cacheable(value = "category",key = "'categoryType:'+#category.type")
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //通过类型 查询菜品或套餐的分类情况
        //先封装查询条件
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType())
                    .orderByAsc(Category::getSort)
                    .orderByDesc(Category::getUpdateTime);
        List<Category> categoryList = categoryService.list(queryWrapper);
        return R.success(categoryList);
    }

}
