/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.library.OnlineBook.controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 * @author huutuan
 */
@Configuration
public class MVCConfig implements WebMvcConfigurer{
     @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path bookUploadDir = Paths.get("./book-image");
        String bookUploadPath = bookUploadDir.toFile().getAbsolutePath();
        
        registry.addResourceHandler("/book-image/**").addResourceLocations("file:/"+bookUploadPath+"/");
    }
}
