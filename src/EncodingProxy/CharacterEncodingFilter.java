package EncodingProxy;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @Author Nuc YongGuang Ji
 * Created by JiYongGuang on 2017/5/4.
 */
@WebFilter(filterName = "CharacterEncodingFilter", urlPatterns = "/*", initParams = {
        @WebInitParam(name = "charsetEncoding", value = "UTF-8")
})
public class CharacterEncodingFilter implements Filter {
    /*
        这种写法是没有办法解决以get方式提交中文参数时的乱码问题的
        只可以对表单中 post形式提交的 中文参数有效。对get方式提交的参数无效
    */
    private FilterConfig filterConfig = null;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        String charset = filterConfig.getInitParameter("charsetEncoding");
        if (charset == null) {
            charset = "UTF-8";
        }

        httpServletRequest.setCharacterEncoding(charset);
        httpServletResponse.setCharacterEncoding(charset);
        httpServletResponse.setContentType("text/html;charset=UTF-8");

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        filterConfig = null;
    }


    private ServletRequest getHttpServletRequestProxy(final HttpServletRequest httpServletRequest) {

        ServletRequest servletRequestProxy = (ServletRequest) Proxy.newProxyInstance(CharacterEncodingFilter.class.getClassLoader(),
                httpServletRequest.getClass().getInterfaces(), new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                        //如果请求方式是get并且调用的是getParameter方法
                        if (httpServletRequest.getMethod().equalsIgnoreCase("get") && method.getName().equals("getParameter")) {
                            //调用原方法的getParameter方法获取 未处理的请求参数的值
                            String value = (String) method.invoke(httpServletRequest, args);
                            if (value == null) {
                                return null;
                            }
                            //进行处理
                            return new String(value.getBytes("iso8859-1"), "UTF-8");
                        } else {//如果请求方式不是get方式 或者 调用的并不是httpServletRequest的getParameter方法那么不处理。
                            return method.invoke(httpServletRequest, args);
                        }

                    }
                });

        return servletRequestProxy;
    }

}
