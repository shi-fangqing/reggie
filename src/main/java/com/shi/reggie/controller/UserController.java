package com.shi.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shi.reggie.common.R;
import com.shi.reggie.entity.User;
import com.shi.reggie.exception.CustomException;
import com.shi.reggie.service.UserService;
import com.shi.reggie.utils.ValidateCodeUtils;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Resource
    private JavaMailSender mailSender;

    //发送方
    @Value("${spring.mail.username}")
    private String from;

    @Resource
    private UserService userService;

    /**
     * 发送验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMail(@RequestBody User user, HttpSession session){
        String phone = user.getPhone();
        try {
            if(StringUtils.isNotEmpty(phone)) {
                //生成四位随机数字
                String code = ValidateCodeUtils.generateValidateCode(4).toString();
                log.info("code=" + code);
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message);
                helper.setFrom(from);
                helper.setTo(phone);
                helper.setSubject("登录验证码");
                //第二个参数为true 表示邮件正文是html格式的，默认是false
                helper.setText("本次验证码为:" + code + "，注意妥善保管，请在五分钟内使用。");
//                mailSender.send(helper.getMimeMessage());   //发送邮件
                session.setAttribute("user",user.getId());
                session.setAttribute("verificationCode",code);
                return R.success("发送成功");
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return R.error("发送失败");
    }

    /**
     * 用户端登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String,String> map,HttpSession session){
        String phone = map.get("phone");
        String code = map.get("code");
        //3. 最后将存入的对象 取出，将id存入session，返回对象
        //1. 先判断 传入的code 和 session中的code 是否相等
        String verificationCode =session.getAttribute("verificationCode").toString();
        if(verificationCode==null||!verificationCode.equals(code)){
            throw new CustomException("验证码错误");
        }
        //2. 判断 数据库中是否 已存在该手机号，如果存在，将信息存入session，返回对象；
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        if (user!=null){
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        //否则，创建对象，存入数据库。
        user=new User();
        user.setPhone(phone);
        userService.save(user);
        session.setAttribute("user",user.getId());
        return R.success(user);

    }

    @PostMapping("/logout")
    public R<String> logout(HttpSession session){
        session.invalidate();
        return R.success("退出成功");
    }
}
