package com.whaler.oasys.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration // 表示这是一个配置类，可以用来定义Bean等配置信息
@EnableAsync // 启用异步注解处理，即支持使用@Async注解的方法
public class AsyncConfig {
    
    /**
     * 定义并配置一个名为"multiThreadExecutor"的线程池 Bean。
     * 
     * @return Executor 返回配置好的线程池实例，用于异步任务的执行。
     */
    @Bean("multiThreadExecutor")
    public Executor multiThreadExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 核心线程池配置
        executor.setCorePoolSize(10); // 核心线程数设置为10个
        
        // 最大线程池大小配置
        executor.setMaxPoolSize(20); // 最大线程数设置为20个
        
        // 队列容量配置
        executor.setQueueCapacity(500); // 设置队列容量为500个任务
        
        // 线程空闲时间配置
        executor.setKeepAliveSeconds(60); // 线程空闲60秒后会被回收
        
        // 线程名前缀配置
        executor.setThreadNamePrefix("do-something-"); // 为线程命名提供前缀，便于识别
        
        // 拒绝策略配置
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy()); // 当队列满了时，直接拒绝执行新的任务
        
        executor.initialize(); // 初始化线程池
        return executor;
    }
}
