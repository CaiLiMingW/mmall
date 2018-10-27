package proxy.Jdkproxy;

import proxy.inteface.Animal;
import proxy.inteface.Aspect;
import proxy.inteface.tiger;

import java.lang.reflect.Proxy;

/**
 * @author Ccc
 * @date 2018/10/25 0025 上午 10:44
 */
public class JdkClient {

    public static void main(String[] args) {
        //ClassLoder 当前类加载器,将其加载到内存
        //Class<>[] 代理类实现的接口
        //处理类，必须进行实现类
        Animal tiger = (Animal) Proxy.newProxyInstance(JdkClient.class.getClassLoader(),new Class[]{Animal.class},new JdkProxy(new tiger(),new Aspect()));
        tiger.eat();
        tiger.speak();
    }
}
