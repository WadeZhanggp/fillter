package fillter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import java.io.IOException;

/**
 * @Author Nuc YongGuang Ji
 * Created by JiYongGuang on 2017/5/2.
 */

@WebFilter(filterName = "MyFilter2", urlPatterns = {"/b.jsp"},
        dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.FORWARD})
public class MyFilter2 implements Filter {

    private FilterConfig filterConfig;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void destroy() {
        //在这个方法中，可以释放过滤器使用的资源
        System.out.println("destroy had down");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("MyFilter2 ======= b.jsp ======= start");
        filterChain.doFilter(servletRequest, servletResponse);
        System.out.println("MyFilter2 ======= b.jsp ======= end");
    }
}
