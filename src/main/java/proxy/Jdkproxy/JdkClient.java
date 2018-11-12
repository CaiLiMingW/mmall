package proxy.Jdkproxy;

import org.apache.commons.lang3.StringUtils;
import proxy.inteface.Animal;
import proxy.inteface.Aspect;
import proxy.inteface.tiger;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ccc
 * @date 2018/10/25 0025 上午 10:44
 */
public class JdkClient {

    public static void main(String[] args) {
        byte a = (byte) ((6*Math.random())+1);
        HashMap map = new HashMap();
        map.put("xx","xxx");
        System.out.println(map.get("xx"));

        String s = "1";

        Integer ss = Integer.valueOf(s);

        //ClassLoder 当前类加载器,将其加载到内存
        //Class<>[] 代理类实现的接口
        //处理类，必须进行实现类
        Animal tiger = (Animal) Proxy.newProxyInstance(JdkClient.class.getClassLoader(),new Class[]{Animal.class},new JdkProxy(new tiger(),new Aspect()));
        tiger.eat();
        tiger.speak();
    }
}
