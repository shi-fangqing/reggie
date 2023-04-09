package com.shi.reggie.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("setmeal_dish")
public class SetmealDish implements Serializable {

    @Serial
    private static final long serialVersionUID = -5164609890943064492L;

    @TableId
    private Long id;

    private Long setmealId; //套餐id

    private Long dishId;  //菜品id

    private String name;  //菜品name

    private Double price; //菜品价格

    private Integer copies;  //套餐内 菜品的份数

    private Integer sort;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

}
