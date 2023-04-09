package com.shi.reggie.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("address_book")
public class AddressBook implements Serializable {

    @Serial
    private static final long serialVersionUID = 8311914521723936853L;

    @TableId
    private Long id;

    private Long userId;

    private String consignee;  //收货人

    private Integer sex;

    private String phone;

    private String provinceCode;

    private String provinceName;

    private String cityCode;

    private String cityName;

    private String districtCode; //地区码

    private String districtName;

    private String detail;

    private String label;

    private Integer isDefault;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

    @TableLogic(value = "0",delval = "1")
    private Integer isDeleted;
}
