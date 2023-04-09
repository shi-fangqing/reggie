package com.shi.reggie.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@TableName("order_detail")
public class OrderDetail implements Serializable {


    @Serial
    private static final long serialVersionUID = 3026161164331613238L;

    private Long id;

    private String name;

    private String image;

    private Long orderId;

    private Long dishId;

    private Long setmealId;

    private String dishFlavor;

    private Integer number;

    private Double amount;


}
