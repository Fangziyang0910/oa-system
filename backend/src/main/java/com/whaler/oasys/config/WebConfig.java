package com.whaler.oasys.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.whaler.oasys.security.LoginInterceptor;

@Configuration
public class WebConfig
implements WebMvcConfigurer {
    private static final String[] EXCLUDE_PATH = {"/user/login", "/user/register","/user/validateToken" ,"/admin/login", "/admin/login", "/admin/validateToken"};
    private static final String API_PTTERN= "/user";

    @Bean
    public CorsFilter corsFilter(){
        // 跨域设置，允许浏览器访问 /API/** 的内容
        UrlBasedCorsConfigurationSource source=
        new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
            "/**", 
            buildConfig()
        );
        return new CorsFilter(source);
    }
    private CorsConfiguration buildConfig(){
        CorsConfiguration corsConfiguration=
        new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        return corsConfiguration;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor())
        .addPathPatterns(
            "/user/**",
            "/admin/**",
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
