package com.shi.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shi.reggie.dto.SetmealDto;
import com.shi.reggie.entity.SetmealDish;
import com.shi.reggie.mapper.SetmealDishMapper;
import com.shi.reggie.service.SetmealDishService;
import org.springframework.stereotype.Service;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
