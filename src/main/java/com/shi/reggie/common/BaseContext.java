package com.shi.reggie.common;

/**
 * 基于ThreadLocal 获取当前线程中 用户的id
 */
public class BaseContext{
    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
