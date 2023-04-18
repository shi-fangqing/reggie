package com.shi.reggie.controller;

import com.shi.reggie.common.Constant;
import com.shi.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    @PostMapping("/upload")
    public R<String> upload(@RequestBody MultipartFile file){
        log.info("上传的文件名:"+file.getOriginalFilename()); //百度.png
        String newFileName=null;
        try {
            String originalFilename = file.getOriginalFilename();
            //为上传的文件创建一个名字，用于保存到服务器端
           newFileName=UUID.randomUUID().toString() +originalFilename.substring(originalFilename.lastIndexOf("."));
            //想要保存的位置,判断是否有该目录，没有则创建
            File dir=new File(Constant.BASE_PATH);
            if(!dir.exists()){
                dir.mkdirs();
            }
            //保存文件到指定位置
            file.transferTo(new File(Constant.BASE_PATH+newFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(newFileName);
    }

    @GetMapping("/download")
    public void download(@RequestParam String name, HttpServletResponse response){
        try {
            //从服务器中找name文件
            File file=new File(Constant.BASE_PATH+name);
            //读此文件到内存
            FileInputStream inputStream=new FileInputStream(file);
            //将读到的文件输出到客户端
            ServletOutputStream outputStream = response.getOutputStream();
            int len=0;
            byte[] bytes=new byte[1024];
            while ((len=inputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
