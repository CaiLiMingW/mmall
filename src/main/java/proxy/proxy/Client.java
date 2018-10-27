package proxy.proxy;

import proxy.inteface.Animal;
import proxy.inteface.tiger;

/**
 * @author Ccc
 * @date 2018/10/25 0025 上午 10:57
 */
public class Client {
    public static void main(String[] args) {
        Animal tiger = new proxy(new tiger());
        tiger.eat();
        tiger.speak();
    }
}
