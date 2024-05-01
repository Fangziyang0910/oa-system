package com.whaler.oasys.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.whaler.oasys.security.LoginInterceptor;

@Configuration
public class WebConfig
implements WebMvcConfigurer {
    private static final String[] EXCLUDE_PATH = {"/user/login", "/user/register", "/admin/login", "/admin/login"};
    private static final String API_PTTERN= "/user";
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor())
        .addPathPatterns(
            "/user/**",
            "/administrator/**",
            "/operator/**",
            "/approver/**",
            "/applicant/**"
        )
        .excludePathPatterns(EXCLUDE_PATH);
    }

    @Bean
    public LoginInterceptor loginInterceptor() {
        return new LoginInterceptor();
    }
    
}
