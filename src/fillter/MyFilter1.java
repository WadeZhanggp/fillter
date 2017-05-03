package fillter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
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

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        System.out.println(httpServletRequest.getScheme());//http
        System.out.println(httpServletRequest.getServerName());//localhost
        System.out.println(httpServletRequest.getServerPort());//8080
        //看你请求的内容的路径，直接对存放在web-root目录下的资源文件访问为空。
        // 创建了新的文件夹并把资源放进去之后对资源文件的访问 其返回值为：/创建的文件夹的名称。
        System.out.println(httpServletRequest.getContextPath());

        filterChain.doFilter(servletRequest, servletResponse);

        System.out.println("MyFilter1 end");
    }
}
