package com.whaler.oasys.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.whaler.oasys.security.LoginInterceptor;

@Configuration // 表示这是一个配置类，通常用于配置Spring MVC的组件
public class WebConfig
implements WebMvcConfigurer {
    // 定义不需要进行拦截的路径
    private static final String[] EXCLUDE_PATH = {"/user/login", "/user/register","/user/validateToken" ,"/admin/login", "/admin/login", "/admin/validateToken"};
    // 定义API的通用前缀
    // private static final String API_PTTERN= "/user";

    /**
     * 配置跨域过滤器。
     * @return CorsFilter 返回一个CorsFilter实例，用于处理跨域请求。
     */
    @Bean
    public CorsFilter corsFilter(){
        // 创建并配置跨域请求源
        UrlBasedCorsConfigurationSource source= 
        new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
            "/**", 
            buildConfig()
        );
        return new CorsFilter(source);
    }

    /**
     * 构建跨域配置。
     * @return CorsConfiguration 返回一个允许所有请求的跨域配置实例。
     */
    private CorsConfiguration buildConfig(){
        CorsConfiguration corsConfiguration=
        new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*"); // 允许所有来源
        corsConfiguration.addAllowedHeader("*"); // 允许所有头部信息
        corsConfiguration.addAllowedMethod("*"); // 允许所有请求方法
        return corsConfiguration;
    }

    /**
     * 配置拦截器。
     * @param registry 拦截器注册表，用于注册和配置拦截器。
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册登录拦截器，并配置拦截的路径及排除路径
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

    /**
     * 创建登录拦截器实例。
     * @return LoginInterceptor 返回一个LoginInterceptor实例，用于拦截未登录的请求。
     */
    @Bean
    public LoginInterceptor loginInterceptor() {
        return new LoginInterceptor();
    }
    
}
