package com.shi.reggie.dto;

import com.shi.reggie.entity.Setmeal;
import com.shi.reggie.entity.SetmealDish;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {
    private List<SetmealDish> setmealDishes=new ArrayList<>();
    private String categoryName;
}
