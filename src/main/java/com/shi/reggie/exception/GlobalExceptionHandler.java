package com.shi.reggie.exception;

import com.shi.reggie.common.R;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice(annotations = {RestController.class, Controller.class})
public class GlobalExceptionHandler {

    /**
     * Duplicate entry 'zhangsan' for key 'employee.idx_username'
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    @ResponseBody
    public R<String> sqlException(SQLIntegrityConstraintViolationException ex){
        String msg = ex.getMessage();
        String[] strings = msg.split(" ");
        if(msg.contains("Duplicate entry")) {
            msg = strings[2] + "已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }

    /**
     * 自定义异常处理
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(CustomException.class)
    public R<String> customException(CustomException ex){
        return R.error(ex.getMessage());
    }
}
