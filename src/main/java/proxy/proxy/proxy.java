package proxy.proxy;

import proxy.inteface.Animal;
import proxy.inteface.Aspect;

/**
 * @author Ccc
 * @date 2018/10/25 0025 上午 10:54
 * 静态代码,代理类代码需要重复编写
 *
 * 基于接口实现静态代理:
 * 目标注入代理类
 */
public class proxy implements Animal {

    private Animal animal;

    private Aspect aspect = new Aspect();

    public proxy(Animal animal) {
        this.animal = animal;
    }

    //静态代理问题:每新增方法,重复代码多
    @Override
    public void eat(){
        aspect.before();
        animal.eat();
        aspect.after();
    }

    @Override
    public void speak() {
        aspect.before();
        animal.speak();
        aspect.after();
    }

}
