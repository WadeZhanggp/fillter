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
 */
@WebFilter(filterName = "GzipFilter", urlPatterns = "")
public class GzipFilter implements Filter {

    private FilterConfig filterConfig;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        final HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        final PrintWriter pw = new PrintWriter(new OutputStreamWriter(bout, "UTF-8"));

        filterChain.doFilter(servletRequest, getHttpServletResponseProxy(httpServletResponse, bout, pw));
        pw.close();

        //拿到目标资源的输出
        byte result[] = bout.toByteArray();
        System.out.println("原始大小:" + result.length);


        ByteArrayOutputStream bout2 = new ByteArrayOutputStream();
        GZIPOutputStream gout = new GZIPOutputStream(bout2);
        gout.write(result);
        gout.close();

        //拿到目标资源输出的压缩数据
        byte[] gzip = bout2.toByteArray();
        System.out.println("压缩大小:" + gzip.length);

        httpServletResponse.setHeader("content-encoding", "gzip");
        httpServletResponse.setContentLength(gzip.length);
        httpServletResponse.getOutputStream().write(gzip);

    }

    @Override
    public void destroy() {
        this.filterConfig = null;
    }

    private ServletResponse getHttpServletResponseProxy(
            final HttpServletResponse httpServletResponse,
            final ByteArrayOutputStream bout,
            final PrintWriter pw) {

        return (ServletResponse) Proxy.newProxyInstance(GzipFilter.class.getClassLoader(), httpServletResponse.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                httpServletResponse.getOutputStream()
                if (method.getName().equals("getWriter")) {
                    return pw;
                } else if (method.getName().equals("getOutputStream")) {
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

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }
    }

}
