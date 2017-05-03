package fillter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import java.io.IOException;

/**
 * @Author Nuc YongGuang Ji
 * Created by JiYongGuang on 2017/5/2.
 */
@WebFilter(filterName = "MyFilter1", urlPatterns = {"/index.jsp"}, initParams = {
        @WebInitParam(name = "charset", value = "UTF-8", description = "character encoding")
}, servletNames = {"FilterServlet"})//servletNames指定该filter所拦截的servlet的名字
public class MyFilter1 implements Filter {//urlPatterns指定该filter所拦截的页面请求路径


    private FilterConfig filterConfig;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void destroy() {
        System.out.println("destroy");

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        System.out.println("MyFilter1 start");

        filterChain.doFilter(servletRequest, servletResponse);

        System.out.println("MyFilter1 end");
    }
}
