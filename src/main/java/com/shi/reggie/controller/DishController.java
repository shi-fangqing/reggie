package com.shi.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shi.reggie.common.R;
import com.shi.reggie.dto.DishDto;
import com.shi.reggie.entity.Category;
import com.shi.reggie.entity.Dish;
import com.shi.reggie.entity.DishFlavor;
import com.shi.reggie.service.CategoryService;
import com.shi.reggie.service.DishFlavorService;
import com.shi.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    @Resource
    private DishService dishService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private DishFlavorService dishFlavorService;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 分页查询菜品信息
     * 用到多表联查
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Cacheable(value = "dish",key = "'page:'+#page+'_'+#pageSize+'_'+#name")
    @GetMapping("/page")
    public R<Page<DishDto>> getPage(@RequestParam Integer page, @RequestParam Integer pageSize, @RequestParam(required = false) String name){
        //根据限制条件，分页查询
        Page<Dish> dishPage = dishService.page(new Page<>(page, pageSize), new LambdaQueryWrapper<Dish>()
                .like(StringUtils.hasLength(name), Dish::getName, name)
                .orderByAsc(Dish::getCategoryId)
                .orderByAsc(Dish::getSort));

        //拷贝 页面的基本信息到 DTO， 忽略掉 records
        Page<DishDto> dishDtoPage=new Page<>();
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");

        //为 DTO 封装 categoryName
        List<DishDto> dishDtoList = dishPage.getRecords().stream().map(record -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(record, dishDto);
            Category category = categoryService.getById(record.getCategoryId());
            dishDto.setCategoryName(category.getName());
            return dishDto;
        }).collect(Collectors.toList());

        //重新装配页面的 records
        dishDtoPage.setRecords(dishDtoList);
        return R.success(dishDtoPage);
    }

    /**
     * 查询菜品信息，包含口味信息
     * @param dish
     * @return
     */
    @Cacheable(value = "dish",key = "#dish.categoryId+'_'+#dish.name+'_1'")
    @GetMapping("/list")
    public R<List<DishDto>> getDishByCategoryId(Dish dish){
        log.info("dish:\t"+dish);
        //通过categoryId或dishName 查询该系列的菜品 注意：只查询启售菜品
        List<Dish> dishList = dishService.list(new LambdaQueryWrapper<Dish>()
                .eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId())
                .like(dish.getName() != null, Dish::getName, dish.getName())
                .eq(Dish::getStatus,1));

        List<DishDto> dishDtoList = dishList.stream().map(dish1 -> {
            //查询菜品对应的口味信息
            List<DishFlavor> flavorList = dishFlavorService.list(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, dish1.getId()));
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish1, dishDto);
            dishDto.setFlavors(flavorList);
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dishDtoList);
    }
    /**
     * 添加菜品
     * @param dishDto
     * @return
     */
    @CacheEvict(value = "dish",allEntries = true)
    @PostMapping
    public R<String> saveDish(@RequestBody DishDto dishDto){
        log.info("dishDto:"+dishDto);
        dishService.saveDishWithFlavor(dishDto);
        return R.success("保存成功");
    }

    /**
     * 修改菜品信息
     * @param dishDto
     * @return
     */
    @CacheEvict(value = "dish",allEntries = true)
    @PutMapping
    public R<String> updateDish(@RequestBody DishDto dishDto){
        dishService.updateDishWithFlavor(dishDto);
        return R.success("修改成功");
    }
    /**
     * 修改菜品时，用于回显数据
     * @param id
     * @return
     */
    @Cacheable(value = "dish",key = "'dishId:'+#id")
    @GetMapping("/{id}")
    public R<DishDto> getDishById(@PathVariable Long id){
        log.info("菜品id="+id);
        DishDto dishDto = dishService.getDishWithFlavorById(id);
        return R.success(dishDto);
    }

    /**
     * 批量更新菜品的状态
     * @param status
     * @param ids
     * @return
     */
    @CacheEvict(value = "dish",allEntries = true)
    @PostMapping("/status/{status}")
    public R<String> updateDishStatus(@PathVariable Integer status,@RequestParam List<Long> ids){
        //批量修改状态
        dishService.update(new LambdaUpdateWrapper<Dish>()
                        .set(Dish::getStatus,status)
                        .in(Dish::getId,ids));
        if (status==0) return R.success("停售成功");
        return R.success("启售成功");
    }

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    @CacheEvict(value = "dish",allEntries = true)
    @DeleteMapping
    public R<String> remove(@RequestParam List<Long> ids){
        dishService.removeDishWithFlavor(ids);
        return R.success("删除成功");
    }
}
