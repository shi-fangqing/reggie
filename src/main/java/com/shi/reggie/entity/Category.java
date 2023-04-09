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
@TableName("category")
public class Category implements Serializable {
    @Serial
    private static final long serialVersionUID = -3363719374702089242L;

    private Long id;

    //类型 1：菜品分类  2：套餐分类
    private Integer type;

    //分类名称
    private String name;

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
