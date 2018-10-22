package org.clm.rabbitmq.test.spring;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 消费者
 * @author zhijun
 *
 */

public class Foo {


    //具体执行业务的方法
     public void listen(String foo) {

        System.out.println("消费者： " + foo);
    }
}