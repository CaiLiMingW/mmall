package org.clm.rabbitmq.test.spring;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rabbitmq.client.ConnectionFactory;
import com.sun.org.apache.bcel.internal.generic.DADD;
import org.clm.Dao.UserMapper;
import org.clm.Pojo.User;
import org.clm.util.JsonUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 消费者
 * @author zhijun
 *
 */

public class Foo {

    @Autowired
    private UserMapper userMapper;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date date1 = new Date();

    private Date date2 = new Date();

    //具体执行业务的方法
     public void listen(String foo) {

        System.out.println("消费者： " + foo);
    }


    @Test
    public  void main() {
        String s1 =date1.toString();
        String s2 = date2.toString();
        Date date1 = JsonUtil.StringToObj(s1, Date.class);
        Date date2 = JsonUtil.StringToObj(s2, Date.class);
        System.out.println("11");
    }
}