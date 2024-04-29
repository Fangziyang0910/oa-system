package com.whaler.oasys.config;

import javax.sql.DataSource;

import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.common.engine.impl.EngineConfigurator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;

@SpringBootConfiguration
public class DataSourceConfig
implements EngineConfigurator {

    @Value("${myflowable.datasource.url}")
    private String url;

    @Value("${myflowable.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${myflowable.datasource.username}")
    private String username;

    @Value("${myflowable.datasource.password}")
    private String password;

    @Override
    public void beforeInit(AbstractEngineConfiguration engineConfiguration) {
        DataSource dataSource = DataSourceBuilder.create()
                .driverClassName(driverClassName)
                .url(url)
                .username(username)
                .password(password).build();
        engineConfiguration.setDataSource(dataSource);
    }

    @Override
    public void configure(AbstractEngineConfiguration engineConfiguration) {
    }

    @Override
    public int getPriority() {
        return 600000; // 保证该优先级最高
    }
}
