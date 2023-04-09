package com.shi.reggie.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.shi.reggie.dto.SetmealDto;
import com.shi.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    void saveSetmealWithDish(SetmealDto setmealDto);

    void removeSetmealWithDish(List<Long> ids);

    SetmealDto getSetmealWithDishById(Long id);

    void updateSetmealWithDish(SetmealDto setmealDto);
}
