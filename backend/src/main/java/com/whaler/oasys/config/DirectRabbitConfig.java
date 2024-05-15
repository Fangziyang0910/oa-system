package com.whaler.oasys.config;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.whaler.oasys.model.entity.UserEntity;
import com.whaler.oasys.service.UserService;

@Configuration
public class DirectRabbitConfig {
    @Autowired
    private UserService userService;

    public static final String RABBITMQ_DIRECT_EXCHANGE = "rabbitmq.direct.exchange";
    public static final String RABBITMQ_TOPIC = "rabbitmq.topic.";
    public static final String RABBITMQ_DIRECT_ROUTING = "rabbitmq.direct.routing.";


    @Bean
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setPort(5672);
        return connectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public Queue rabbitmqDirectQueue() {
        RabbitAdmin rabbitAdmin = rabbitAdmin();
        /**
         * 1、name:    队列名称
         * 2、durable: 是否持久化
         * 3、exclusive: 是否独享、排外的。如果设置为true，定义为排他队列。则只有创建者可以使用此队列。也就是private私有的。
         * 4、autoDelete: 是否自动删除。也就是临时队列。当最后一个消费者断开连接后，会自动删除。
         * */
        LambdaQueryWrapper<UserEntity>lambdaQueryWrapper=new LambdaQueryWrapper<>();
        List<UserEntity>userEntities= userService.getBaseMapper().selectList(lambdaQueryWrapper);
        List<String>userNames=userEntities.stream().map(UserEntity::getName).collect(Collectors.toList());
        userNames.forEach(userName->{
            Queue queue=new Queue(DirectRabbitConfig.RABBITMQ_TOPIC+userName, true, false, false);
            
            Binding binding = BindingBuilder.bind(queue)
                .to(rabbitmqDemoDirectExchange())
                .with(RABBITMQ_DIRECT_ROUTING+userName);
            rabbitAdmin.declareBinding(binding);
            rabbitAdmin.declareQueue(queue);
        });
        return null;
    }

    @Bean
    public DirectExchange rabbitmqDemoDirectExchange() {
        //Direct交换机
        return new DirectExchange(DirectRabbitConfig.RABBITMQ_DIRECT_EXCHANGE, true, false);
    }


}
