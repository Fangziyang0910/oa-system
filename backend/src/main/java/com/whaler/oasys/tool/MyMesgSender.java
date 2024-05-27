package com.whaler.oasys.tool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.whaler.oasys.config.DirectRabbitConfig;
import com.whaler.oasys.model.vo.MsgVo;

@Component // 标识为Spring组件，使得Spring容器能够自动发现和装配它
public class MyMesgSender {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 用于格式化日期的SimpleDateFormat实例
    
    @Resource // 标识一个注入的资源，Spring会自动寻找与之匹配的Bean并注入
    private RabbitTemplate rabbitTemplate; // RabbitMQ的模板类，用于发送消息

    /**
     * 发送消息的方法。
     * @param userName 接收消息的用户名称，用于构建消息的路由键。
     * @param msgName 消息的名称。
     * @param msgContent 消息的内容。
     * @return 发送结果，成功返回"ok"，失败返回"error"。
     */
    public String sendMessage(String userName,String msgName,String msgContent){
        try{
            // 生成消息ID，并格式化当前时间
            String msgId=UUID.randomUUID().toString().replace("-", "").substring(0,32);
            String sendTime=sdf.format(new Date());
            
            // 创建消息实体，设置消息ID、发送时间、消息名称和内容
            MsgVo msgVo=new MsgVo();
            msgVo.setMsgId(msgId);
            msgVo.setSendTime(sendTime);
            msgVo.setMsgName(msgName);
            msgVo.setMsgContent(msgContent);
            
            // 使用RabbitTemplate发送消息，路由键由用户名称拼接而成
            rabbitTemplate.convertAndSend(DirectRabbitConfig.RABBITMQ_DIRECT_EXCHANGE, DirectRabbitConfig.RABBITMQ_DIRECT_ROUTING+userName, msgVo);
        }catch(Exception e){
            e.printStackTrace(); // 捕获异常，打印堆栈信息
            return "error";
        }
        return "ok";
    }
}
