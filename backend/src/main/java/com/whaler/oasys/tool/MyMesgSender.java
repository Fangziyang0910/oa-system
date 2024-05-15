package com.whaler.oasys.tool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.whaler.oasys.config.DirectRabbitConfig;

@Component
public class MyMesgSender {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    @Resource
    private RabbitTemplate rabbitTemplate;

    public String sendMessage(String userName,String msg){
        try{
            String msgId=UUID.randomUUID().toString().replace("-", "").substring(0,32);
            String sendTime=sdf.format(new Date());
            Map<String,Object>map=new HashMap<>();
            map.put("msgId", msgId);
            map.put("sendTime", sendTime);
            map.put("msg", msg);
            rabbitTemplate.convertAndSend(DirectRabbitConfig.RABBITMQ_DIRECT_EXCHANGE, DirectRabbitConfig.RABBITMQ_DIRECT_ROUTING+userName, map);
        }catch(Exception e){
            e.printStackTrace();
            return "error";
        }
        return "ok";
    }
}
