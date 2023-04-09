package com.shi.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("dish")
public class Dish implements Serializable {
    @Serial
    private static final long serialVersionUID = 2116594169549287646L;

    private Long id;

    private String name;

    private Long categoryId;

    private Double price;

    private String code;

    private String image;

    private String description;

    private Integer status;

    private Integer sort;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill= FieldFill.INSERT)
    private Long createUser;

    @TableField(fill=FieldFill.INSERT_UPDATE)
    private Long updateUser;

    @TableLogic(value = "0",delval = "1")
    private Integer isDeleted;
}
