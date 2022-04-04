package com.bx.im.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;

/**
 * 文件上传相关配置
 */
// @Configuration
public class UploadFileConfig {

    @Value("${file.uploadFolder}")
    private String uploadFolder;

    // @Bean
    // MultipartConfigElement multipartConfigElement() {
    //     MultipartConfigFactory factory = new MultipartConfigFactory();
    //     factory.setLocation(uploadFolder);
    //     //文件最大
    //     factory.setMaxFileSize(DataSize.parse("100MB"));
    //     // 设置总上传数据总大小
    //     factory.setMaxRequestSize(DataSize.parse("200MB"));
    //     return factory.createMultipartConfig();
    // }
}
