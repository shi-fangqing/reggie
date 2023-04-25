package com.shi.reggie.config;


import ch.qos.logback.classic.pattern.MessageConverter;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.shi.reggie.common.JacksonObjectMapper;
import com.shi.reggie.interceptor.LoginInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Configuration
@EnableSwagger2
@EnableKnife4j
public class WebMvcConfig extends WebMvcConfigurationSupport {

    @Resource
    private LoginInterceptor loginInterceptor;

    /**
     * 开启静态资源的访问路径映射
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开启静态资源映射....");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * 扩展MVC的消息转换器
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建一个消息转换器对象
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson将Java对象转为json
        mappingJackson2HttpMessageConverter.setObjectMapper(new JacksonObjectMapper());
        //将消息转换器添加到转换器集合中，放到第一个位置
        converters.add(0,mappingJackson2HttpMessageConverter);
    }

    /**
     * 添加拦截器，放行静态资源和登录请求
     * @param registry
     */
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/employee/login",
                        "/employee/logout",
                        "/backend/**",
                        "/front/**",
                        "/user/login",
                        "/user/logout",
                        "/user/sendMsg",
                        "/doc.html",
                        "/webjars/**",
                        "/swagger-resources",
                        "/v2/api-docs");
    }

    /**
     * Swagger接口管理
     * @return
     */
    @Bean
    public Docket createRestApi(){
        //文档类型
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder().title("瑞吉外卖").version("1.0").description("瑞吉外卖接口文档").build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.shi.reggie.controller"))
                .paths(PathSelectors.any())
                .build();
    }
}
