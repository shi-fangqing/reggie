package com.shi.reggie.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@TableName("user")
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = -3609258951124281473L;

    @TableId
    private Long id;

    private String name;

    private String phone;

    private Character sex;

    private String idNumber; //身份证号

    private String avatar;  //头像

    private Integer status;
}
