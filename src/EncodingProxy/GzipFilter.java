package EncodingProxy;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.zip.GZIPOutputStream;

/**
 * @Author Nuc YongGuang Ji
 * Created by JiYongGuang on 2017/5/5.
 */                                  //该过滤器会对这些 文件里的文本内容经过压缩过后再输出到客户端显示
@WebFilter(filterName = "GzipFilter", urlPatterns = {"*.jsp", "*.js", "*.css", "*.html"}, dispatcherTypes = {DispatcherType.FORWARD, DispatcherType.REQUEST})
public class GzipFilter implements Filter {

    private FilterConfig filterConfig;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        //声明为 final 类型的变量。变量初始化时就必须赋值，且值不能再被修改及其之类对其覆盖
        final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        final HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        //字节数组输出流 用来压缩数据
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        //创建一个新的 PrintWriter
        //将 ByteArrayOutputStream 传话成UTF-8的编码格式存出进去
        final PrintWriter pw = new PrintWriter(new OutputStreamWriter(bout, "UTF-8"));

                                            //做完前缀准备后，调用原方案。参数更改为:生成的代理 servletResponse 对象
        filterChain.doFilter(servletRequest, getHttpServletResponseProxy(httpServletResponse, bout, pw));
        pw.close();



        //原方案执行过后。拿到目标资源的输出对象
        byte result[] = bout.toByteArray();
        System.out.println("原始大小:" + result.length);


        ByteArrayOutputStream bout2 = new ByteArrayOutputStream();
        //一个具有默认缓冲区大小的新输出流
        GZIPOutputStream gout = new GZIPOutputStream(bout2);
        //将 b.length 个字节写入此输出流 gout 生成 bout2的压缩输出流gout 。将 result 进行压缩写入 bout2。
        gout.write(result);
        gout.close();

        //拿到目标资源输出的压缩数据
        byte[] gzip = bout2.toByteArray();
        System.out.println("压缩大小:" + gzip.length);

        //上下文内容读取到的是 Gzip压缩处理后的内容
        httpServletResponse.setHeader("content-encoding", "gzip");
        httpServletResponse.setContentLength(gzip.length);
        //压缩后的内容写入输出流
        httpServletResponse.getOutputStream().write(gzip);

    }

    @Override
    public void destroy() {
        this.filterConfig = null;
    }

    //生成代理对象的方法，一般必须传入你所想要生成的代理对象原实体
    private ServletResponse getHttpServletResponseProxy(
            final HttpServletResponse httpServletResponse,
            final ByteArrayOutputStream bout,//用来压缩数据
            final PrintWriter pw) {

        return (ServletResponse) Proxy.newProxyInstance(GzipFilter.class.getClassLoader(), httpServletResponse.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                httpServletResponse.getOutputStream()
                if (method.getName().equals("getWriter")) {//如果调用的是Response的getWriter方法 返回你实例化生成的PrintWriter对象
                    return pw;
                } else if (method.getName().equals("getOutputStream")) {
                    //返回一个 ServletOutputStream 类的子类。用户并不知道
                    //重要的是参数 bout 是我们传递进去的字节数组输出流.
                    //所以响应输出的数据都保存在该输出流中，后序执行完之后我们可以拿到该 bout输出流 对其压缩处理
                    return new MyServletOutputStream(bout);
                } else
                    return method.invoke(httpServletResponse, args);
            }
        });
    }

    class MyServletOutputStream extends ServletOutputStream {

        private ByteArrayOutputStream bout = null;

        public MyServletOutputStream(ByteArrayOutputStream bout) {
            this.bout = bout;
        }

        @Override
        public void write(int b) throws IOException {
            bout.write(b);
        }

        //必须实现的方法。可不写任何自定义的实现体
        @Override
        public boolean isReady() {
            return false;
        }

        //必须实现的方法。可不写任何自定义的实现体
        @Override
        public void setWriteListener(WriteListener writeListener) {

        }
    }

}
