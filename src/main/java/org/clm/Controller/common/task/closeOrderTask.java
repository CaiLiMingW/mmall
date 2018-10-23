package org.clm.Controller.common.task;

import com.rabbitmq.client.ConnectionFactory;
import org.clm.Service.IOrderService;
import org.clm.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * @author Ccc
 * @date 2018/10/15 0015 下午 7:43
 */
@Component
public class closeOrderTask {

    @Autowired
    private IOrderService iOrderService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final Logger log = LoggerFactory.getLogger(closeOrderTask.class);
    @Scheduled(cron="0 0/1 * * * ? ")
    public void closeOrderTaskv(){
        //TODO 定时关单

            //RabbitMQ模板
            //发送消息
//        rabbitTemplate.convertAndSend("Hello, world!");
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.time"));
        iOrderService.closeOrderTask(hour);
        System.out.println(1);
        log.info("定时任务");
    }

//    @Scheduled(cron="0 0 0 1 * ? ")
@Scheduled(cron="0 0 0 1 * ? ")
    public void delCloseOrderTask(){
        iOrderService.dedelCloseOrderTask();
        log.info("定时删除订单");
    }

}
