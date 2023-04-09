package com.shi.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shi.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
