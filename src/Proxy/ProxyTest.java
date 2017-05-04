package Proxy;

/**
 * @Author Nuc YongGuang Ji
 * Created by JiYongGuang on 2017/5/3.
 */
public class ProxyTest {

    public static void main(String[] args) {

        LiuDeHuaProxyCopy proxy = new LiuDeHuaProxyCopy();

        Person p = proxy.getProxy();//对Person接口的下层实现类 生成了一个代理对象

        //把该生成的代理类当成接口的实现类使用即可。
        //无论调用真实的实现类的任何方法 都会触发该代理类中声明的 InvocationHandler(处理器) 的invoke方法。
        String retValue = p.sing("冰雨");
        System.out.println(retValue);

        //注意调用invoke 方法的时候需要注意的仓额参数
        // p.dance("江南style");
        //1.p ---> proxy
        //2.dance ---> method
        //3."江南style" ---> args
        String value = p.dance("江南style");
        System.out.println(value);
    }
}
