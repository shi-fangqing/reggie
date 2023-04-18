package com.shi.reggie.interceptor;

import com.alibaba.fastjson.JSON;
import com.shi.reggie.common.BaseContext;
import com.shi.reggie.common.R;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //管理端是否登陆过
        if (request.getSession()!=null&&request.getSession().getAttribute("employee")!=null){
            BaseContext.setCurrentId((Long) request.getSession().getAttribute("employee"));
            return true;
        }

        //判断用户端是否登陆过
        if(request.getSession()!=null&&request.getSession().getAttribute("user")!=null){
            BaseContext.setCurrentId((Long) request.getSession().getAttribute("user"));
            return true;
        }

        //未登录，响应给前端
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return false;
    }
}
