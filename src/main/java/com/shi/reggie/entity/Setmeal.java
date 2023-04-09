package com.shi.reggie.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("setmeal")
public class Setmeal implements Serializable {

    @Serial
    private static final long serialVersionUID = 378192406382995953L;

    @TableId
    private Long id;

    private String name;

    private Long categoryId;

    private Double price;

    private String code;

    private String image;

    private String description;

    private Integer status;


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
