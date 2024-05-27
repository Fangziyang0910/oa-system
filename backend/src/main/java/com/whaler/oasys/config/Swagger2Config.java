package com.whaler.oasys.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration // 表示这是一个配置类
@EnableSwagger2 // 启用Swagger2
public class Swagger2Config {

    // 读取配置文件中swagger.enabled的值
    @Value("${swagger.enabled}")
    private boolean enabled;

    /**
     * 创建REST API的Docket Bean。
     * 
     * @return Docket配置实例
     */
    @Bean
    public Docket createRestApi(){
        // Docket实例的配置
        return new Docket(DocumentationType.SWAGGER_2) 
                .apiInfo(apiInfo()) // API基本信息
                .select() // 返回一个ApiSelectorBuilder实例，用来控制那些接口暴露给Swagger来展现
                // 以下决定扫描哪些接口
                // .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)) // 按方法上是否有ApiOperation注解来选择
                .apis(RequestHandlerSelectors.basePackage("com.whaler.oasys.controller")) // 按包路径来选择
                .paths(PathSelectors.any()) // 按路径来选择
                .build() 
                .globalOperationParameters(security()); // 全局参数设置
    }

    /**
     * 创建API的基本信息
     * 
     * @return 返回构建好的ApiInfo实例
     */
    private ApiInfo apiInfo(){
        return new ApiInfoBuilder() 
            .title("OA-SYS 接口文档") // 设置文档标题
            .description("具体使用查看 https://juejin.cn/post/7035027705720471583") // 设置文档描述
            .version("1.0") // 设置文档版本号
            .build(); // 构建
    }

    /**
     * 设置全局的安全参数
     * 
     * @return 返回包含全局安全参数的列表
     */
    private List<Parameter> security(){
        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        // 设置令牌的全局安全参数
        Parameter paramater = tokenPar.name("Authorization").description("令牌").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
        pars.add(paramater);
        return pars;
    }
}
