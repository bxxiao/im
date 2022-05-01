package com.bx.im.config;

import com.bx.im.config.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 */
@Configuration
public class ApplicationConfig {

    @Autowired
    private JwtInterceptor globalInterceptor;

    /*
    * 处理跨域请求
    * */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许所有域名进行跨域调用
        // 对应 Access-Control-Allow-Origin 字段返回值
        config.addAllowedOriginPattern("*");
        // 允许跨越发送cookie
        // 对应 Access-Control-Allow-Credentials 字段返回值
        config.setAllowCredentials(true);
        // 放行全部原始头信息
        // 对应 Access-Control-Allow-Headers 字段返回值
        config.addAllowedHeader("*");
        // 允许所有请求方法跨域调用
        // 对应 Access-Control-Allow-Methods 字段返回值
        config.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    /*
    * 拦截器（需要先创建一个WebMvcConfigurer实现类，在其中
    * 注册拦截器和设置
    * */
    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(globalInterceptor).excludePathPatterns("/api/user/login", "/upload/**", "/api/user/registry");
            }
        };
    }
}