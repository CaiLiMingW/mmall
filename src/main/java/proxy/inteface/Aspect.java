package proxy.inteface;

import org.springframework.stereotype.Component;

/**
 * @author Ccc
 * @date 2018/10/25 0025 上午 10:24
 */
@Component
public class Aspect {
    public void before(){
        System.out.println("=============before=============");
    }

    public void after(){
        System.out.println("=============after=============");
    }
}
