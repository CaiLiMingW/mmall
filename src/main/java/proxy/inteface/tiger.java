package proxy.inteface;

/**
 * @author Ccc
 * @date 2018/10/25 0025 上午 9:36
 */
public class tiger implements Animal {
    @Override
    public void eat() {
        System.out.println("tiger:eat");
    }

    @Override
    public void speak() {
        System.out.println("tiger:speak");
    }


}
