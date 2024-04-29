package com.whaler.oasys.config;

import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;

@SpringBootConfiguration
public class ProcessEngineConfig
implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {
    
    @Autowired
    private DataSourceConfig datasourceConfigurator;

    @Override
    public void configure(SpringProcessEngineConfiguration engineConfiguration) {
        engineConfiguration.addConfigurator(datasourceConfigurator);
        engineConfiguration.setDeploymentMode("single-resource");
    }
}
