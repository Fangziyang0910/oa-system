package com.whaler.oasys.config;

import javax.sql.DataSource;

import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.common.engine.impl.EngineConfigurator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;

/**
 * 数据源配置类，用于配置Flowable引擎的数据源。
 * 通过实现EngineConfigurator接口，使得在Flowable引擎初始化之前能够自定义配置。
 */
@SpringBootConfiguration
public class DataSourceConfig
implements EngineConfigurator {

    // 从配置文件中读取数据库URL
    @Value("${myflowable.datasource.url}")
    private String url;

    // 从配置文件中读取数据库驱动类名
    @Value("${myflowable.datasource.driver-class-name}")
    private String driverClassName;

    // 从配置文件中读取数据库用户名
    @Value("${myflowable.datasource.username}")
    private String username;

    // 从配置文件中读取数据库密码
    @Value("${myflowable.datasource.password}")
    private String password;

    /**
     * 在Flowable引擎初始化之前配置数据源。
     * @param engineConfiguration 引擎配置对象，用于设置数据源。
     */
    @Override
    public void beforeInit(AbstractEngineConfiguration engineConfiguration) {
        // 基于配置信息构建数据源
        DataSource dataSource = DataSourceBuilder.create()
                .driverClassName(driverClassName)
                .url(url)
                .username(username)
                .password(password).build();
        // 设置引擎使用该数据源
        engineConfiguration.setDataSource(dataSource);
    }

    /**
     * 配置引擎的其他设置。
     * 本示例中未进行任何配置，可根据实际需要进行扩展。
     * @param engineConfiguration 引擎配置对象。
     */
    @Override
    public void configure(AbstractEngineConfiguration engineConfiguration) {
    }

    /**
     * 获取配置优先级。
     * 通过设置较高的优先级，确保该配置在其他配置之前应用。
     * @return 配置优先级，整数值越大表示优先级越高。
     */
    @Override
    public int getPriority() {
        return 600000; // 设置较高的优先级，以确保数据源配置被优先应用
    }
}
