package com.shi.reggie.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Orders implements Serializable {
    @Serial
    private static final long serialVersionUID = -7039490516779447453L;

    @TableId
    private Long id;

    private String number;

    private Integer status;

    private Long userId;

    private Long addressBookId;

    private LocalDateTime orderTime;

    private LocalDateTime checkoutTime;

    private Integer payMethod;

    private Double amount;

    private String remark;

    private String phone;

    private String address;

    private String userName;

    private String consignee;
}
