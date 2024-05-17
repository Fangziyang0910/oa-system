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
import com.whaler.oasys.model.vo.MsgVo;

@Component
public class MyMesgSender {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    @Resource
    private RabbitTemplate rabbitTemplate;

    public String sendMessage(String userName,String msgName,String msgContent){
        try{
            String msgId=UUID.randomUUID().toString().replace("-", "").substring(0,32);
            String sendTime=sdf.format(new Date());
            MsgVo msgVo=new MsgVo();
            msgVo.setMsgId(msgId);
            msgVo.setSendTime(sendTime);
            msgVo.setMsgName(msgName);
            msgVo.setMsgContent(msgContent);
            rabbitTemplate.convertAndSend(DirectRabbitConfig.RABBITMQ_DIRECT_EXCHANGE, DirectRabbitConfig.RABBITMQ_DIRECT_ROUTING+userName, msgVo);
        }catch(Exception e){
            e.printStackTrace();
            return "error";
        }
        return "ok";
    }
}
