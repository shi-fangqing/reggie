package com.shi.reggie.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用返回结果，服务端响应的数据最后封装成其对象
 * @param <T>
 */
@Data
public class R <T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 4711111668555939017L;

    private Integer code;  //编码：1成功，0和其他数字失败
    private String msg;    //错误信息
    private T data;    //返回数据

    private Map<String,Object> map=new HashMap<>();  //动态数据

    public static <T> R<T> success(T object){
        R<T> r=new R<>();
        r.data=object;
        r.code=1;
        return r;
    }

    public static <T> R<T> error(String msg){
        R<T> r = new R<>();
        r.msg=msg;
        r.code=0;
        return r;
    }

    public R<T> add(String key,Object value){
        this.map.put(key,value);
        return this;
    }

}
