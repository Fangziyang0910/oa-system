package com.whaler.oasys.config;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
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

    public static final String DIRECT_EXCHANGE = "rabbitmq.direct.exchange";

    @Bean
    public List<Queue> rabbitmqDirectQueue() {
        /**
         * 1、name:    队列名称
         * 2、durable: 是否持久化
         * 3、exclusive: 是否独享、排外的。如果设置为true，定义为排他队列。则只有创建者可以使用此队列。也就是private私有的。
         * 4、autoDelete: 是否自动删除。也就是临时队列。当最后一个消费者断开连接后，会自动删除。
         * */
        LambdaQueryWrapper<UserEntity>lambdaQueryWrapper=new LambdaQueryWrapper<>();
        List<UserEntity>userEntities= userService.getBaseMapper().selectList(lambdaQueryWrapper);
        List<String>userNames=userEntities.stream().map(UserEntity::getName).collect(Collectors.toList());
        List<Queue>queues=new ArrayList<>();
        userNames.forEach(userName->{
            queues.add(new Queue("rabbitmq.topic."+userName, true, false, false));
        });
        return queues;
    }

    @Bean
    public DirectExchange rabbitmqDemoDirectExchange() {
        //Direct交换机
        return new DirectExchange(DirectRabbitConfig.DIRECT_EXCHANGE, true, false);
    }

    @Bean
    public List<Binding> bindDirect() {
        //链式写法，绑定交换机和队列，并设置匹配键
        List<Binding> bindings=new ArrayList<>();
        rabbitmqDirectQueue().forEach(queue->{
            bindings.add(
                BindingBuilder.bind(queue)
                .to(rabbitmqDemoDirectExchange())
                .with(queue.getName())
            );
        });
        return bindings;
    }
}
