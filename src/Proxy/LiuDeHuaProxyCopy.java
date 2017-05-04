package Proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @Author Nuc YongGuang Ji
 * Created by JiYongGuang on 2017/5/4.
 */
public class LiuDeHuaProxyCopy {

    // 用变量ldh记录下要代理的目标对象
    private Person ldh = new LiuDeHua();

    public Person getProxy() {
        //getClassLoader。getInterfaces 两个参数用来生成目标的代理对象。作为指定参数被使用
        //3.InvocationHandler 如果不调用该代理对象的方法 不会触发其中的 invoke方法。

        return (Person) Proxy.newProxyInstance(LiuDeHuaProxyCopy.class.getClassLoader(), ldh.getClass().getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                        if (method.getName().equals("sing")) {
                            System.out.println("我是他的经纪人，要找他唱歌得先给十万块钱！！");
                            return method.invoke(ldh, args);//反调用 被代理对象方法，并将参数传递进去
                        }

                        if (method.getName().equals("dance")) {
                            System.out.println("我是他的经纪人，要找他跳舞得先给二十万块钱！！");
                            return method.invoke(ldh, args);
                        }

                        return null;
                    }
                });
    }

}
