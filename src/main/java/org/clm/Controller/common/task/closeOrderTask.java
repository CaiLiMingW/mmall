package org.clm.Controller.common.task;

import org.clm.Service.IOrderService;
import org.clm.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final Logger log = LoggerFactory.getLogger(closeOrderTask.class);
    @Scheduled(cron="0 */5 * * * ? ")
    public void closeOrderTaskv(){
        //TODO 定时关单

        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.time"));
        iOrderService.closeOrderTask(hour);
        System.out.println(1);
        log.info("定时任务");
    }

//    @Scheduled(cron="0 0 0 1 * ? ")
@Scheduled(cron="0 0 1 * * ? ")
    public void delCloseOrderTask(){
        iOrderService.dedelCloseOrderTask();
    }

    public static void main(String[] args) {
        int s = Integer.parseInt("1");
        String property = PropertiesUtil.getProperty("close.order.time");
        int xx = Integer.parseInt(property);
        int x = Integer.valueOf(PropertiesUtil.getProperty("close.order.time","2")).intValue();
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.time"));
    }
}
