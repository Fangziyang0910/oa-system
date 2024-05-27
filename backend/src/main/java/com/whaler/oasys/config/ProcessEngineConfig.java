package com.whaler.oasys.config;

import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;

/**
 * ProcessEngineConfig 是一个配置类，用于配置流程引擎。
 * 它通过实现EngineConfigurationConfigurer接口来定制SpringProcessEngineConfiguration的配置。
 */
@SpringBootConfiguration
public class ProcessEngineConfig
implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {
    
    // 自动装配DataSourceConfig，用于配置数据源
    @Autowired
    private DataSourceConfig datasourceConfigurator;

    /**
     * 配置流程引擎的设置。
     * 
     * @param engineConfiguration 提供一个用于设置流程引擎配置的接口。
     */
    @Override
    public void configure(SpringProcessEngineConfiguration engineConfiguration) {
        // 设置字体为宋体
        engineConfiguration.setActivityFontName("宋体");
        engineConfiguration.setLabelFontName("宋体");
        engineConfiguration.setAnnotationFontName("宋体");
        
        // 添加数据源配置
        engineConfiguration.addConfigurator(datasourceConfigurator);
        
        // 设置部署模式为单资源
        engineConfiguration.setDeploymentMode("single-resource");
        
        // 启用异步执行器
        engineConfiguration.setAsyncExecutorActivate(true);
    }
}
