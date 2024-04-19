package com.whaler.oasys.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.whaler.oasys.security.LoginInterceptor;

// @Configuration
public class WebConfig
implements WebMvcConfigurer {
    private static final String[] EXCLUDE_PATH = {"/login", "/register", "/css/**", "/js/**", "/images/**", "/fonts/**", "/favicon.ico"};
    private static final String API_PTTERN= "/API/**";
    
    @Autowired
    private LoginInterceptor loginInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
        .addPathPatterns(API_PTTERN)
        .excludePathPatterns(EXCLUDE_PATH);
    }

    
}
