package org.clm.Aspect;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author Ccc
 * @date 2018/10/25 0025 上午 10:24
 * 切面类
 */
@Component
@org.aspectj.lang.annotation.Aspect
public class Aspect {
    @Before(value = "myPoincut()")
    public void before(){
        System.out.println("=============before=============");
    }

    @After("myPoincut()")
    public void after(){
        System.out.println("=============after=============");
    }

    @Pointcut("execution(* org.clm.Controller.common.task.closeOrderTask.apsectest(..))")
    private void myPoincut(){

    }
}
