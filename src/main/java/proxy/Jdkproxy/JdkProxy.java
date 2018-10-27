package proxy.Jdkproxy;

import proxy.inteface.Aspect;
import proxy.inteface.Animal;
import proxy.inteface.tiger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author Ccc
 * @date 2018/10/25 0025 上午 9:32
 * 基于接口实现jdk动态代理
 */
//代理类
public class JdkProxy implements InvocationHandler {

    //1.目标类(切入点)
    private Object object;

    //2.切面类(通知)
    Aspect aspect ;

    public JdkProxy(Object object, Aspect aspect) {
        this.object = object;
        this.aspect = aspect;
    }
    //3.代理类：将目标类（切入点）和 切面类（通知） 结合 --> 切面
    //Proxy.newProxyInstance
    //参数1：loader ，类加载器，动态代理类 运行时创建，任何类都需要类加载器将其加载到内存。
    //参数2：Class[] interfaces 代理类需要实现的所有接口
    //      方式1：目标类实例.getClass().getInterfaces()  ;注意：只能获得自己接口，不能获得父元素接口
    //      方式2： new Class[]{UserService.class}
    //参数3：InvocationHandler  处理类，接口，必须进行实现类，一般采用匿名内部
    //提供 invoke 方法，代理类的每一个方法执行时，都将调用一次invoke
    //		 * 				参数31：Object proxy ：代理对象
    //		 * 				参数32：Method method : 代理对象当前执行的方法的描述对象（反射）
    //		 * 					执行方法名：method.getName()
    //		 * 					执行方法：method.invoke(对象，实际参数)
    //		 * 				参数33：Object[] args :方法实际参数

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //前执行
        aspect.before();

        //执行目标类的方法
        //利用反射,动态执行方法
        Object invoke = method.invoke(object, args);

        //后执行
        aspect.after();
        return invoke;
    }
}
