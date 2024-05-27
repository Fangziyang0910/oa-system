package com.whaler.oasys.config;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.whaler.oasys.model.entity.UserEntity;
import com.whaler.oasys.service.UserService;

@Configuration // 标识为配置类
public class DirectRabbitConfig {
    @Autowired
    private UserService userService;

    // RabbitMQ 相关配置常量
    public static final String RABBITMQ_DIRECT_EXCHANGE = "rabbitmq.direct.exchange";
    public static final String RABBITMQ_TOPIC = "rabbitmq.topic.";
    public static final String RABBITMQ_DIRECT_ROUTING = "rabbitmq.direct.routing.";

    /**
     * 创建连接工厂
     * 
     * @return CachingConnectionFactory RabbitMQ的连接工厂
     */
    @Bean
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setPort(5672);
        return connectionFactory;
    }

    /**
     * 创建RabbitAdmin对象
     * 
     * @return RabbitAdmin 用于管理RabbitMQ的admin对象
     */
    @Bean
    public RabbitAdmin rabbitAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    /**
     * 创建队列并绑定到交换器
     * 根据用户服务中的用户列表动态创建队列，并将这些队列绑定到Direct交换机上。
     * 
     * @return Queue 返回null，因为是动态创建多个队列
     */
    @Bean
    public Queue rabbitmqDirectQueue() {
        RabbitAdmin rabbitAdmin = rabbitAdmin();
        
        // 查询用户列表，根据用户名称动态创建队列并绑定到交换器
        LambdaQueryWrapper<UserEntity>lambdaQueryWrapper=new LambdaQueryWrapper<>();
        List<UserEntity>userEntities= userService.getBaseMapper().selectList(lambdaQueryWrapper);
        List<String>userNames=userEntities.stream().map(UserEntity::getName).collect(Collectors.toList());
        userNames.forEach(userName->{
            Queue queue=new Queue(DirectRabbitConfig.RABBITMQ_TOPIC+userName, true, false, false);
            
            rabbitAdmin.declareQueue(queue);
            Binding binding = BindingBuilder.bind(queue)
                .to(rabbitmqDemoDirectExchange())
                .with(RABBITMQ_DIRECT_ROUTING+userName);
            rabbitAdmin.declareBinding(binding);
        });
        return null;
    }

    /**
     * 创建Direct类型的交换器
     * 
     * @return DirectExchange 直接交换机
     */
    @Bean
    public DirectExchange rabbitmqDemoDirectExchange() {
        return new DirectExchange(DirectRabbitConfig.RABBITMQ_DIRECT_EXCHANGE, true, false);
    }

    /**
     * 配置自定义的RabbitTemplate，使用Jackson进行消息的JSON序列化和反序列化
     * 
     * @param connectionFactory RabbitMQ的连接工厂
     * @return RabbitTemplate 配置好的RabbitTemplate对象
     */
    @Bean
    public RabbitTemplate jacksonRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

}
