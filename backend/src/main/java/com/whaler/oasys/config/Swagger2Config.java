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

@Configuration
@EnableSwagger2
public class Swagger2Config {
    @Value("${swagger.enabled}")
    private boolean enabled;

    @Bean
    public Docket createRestApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //加了ApiOperation注解的类，才生成接口文档
                // .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                //包下的类，才生成接口文档
                .apis(RequestHandlerSelectors.basePackage("com.whaler.oasys.controller"))
                .paths(PathSelectors.any())
                .build().globalOperationParameters(security());
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
            .title("OA-SYS 接口文档")
            .description("具体使用查看 https://juejin.cn/post/7035027705720471583")
            // .termsOfServiceUrl("")
            // .contact("ratel")
            .version("1.0")
            .build();
    }

    private List<Parameter> security(){
        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        Parameter paramater = tokenPar.name("Authorization").description("令牌").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
        pars.add(paramater);
        return pars;
    }
}
