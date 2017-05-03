package fillter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

/**
 * @Author Nuc YongGuang Ji
 * Created by JiYongGuang on 2017/5/2.
 */
@WebFilter(filterName = "SessionFilter", urlPatterns = "/*", initParams = {
        @WebInitParam(name = "logonStrings", value = "/project/index.jsp;login.do", description = "对登录页面不进行过滤"),
        @WebInitParam(name = "includeStrings", value = ".do;.jsp", description = "只对指定过滤参数后缀进行过滤"),
        @WebInitParam(name = "redirectPath", value = "/index.jsp", description = "未通过跳转到登录界面"),
        @WebInitParam(name = "disabletestfilter", value = "N", description = "Y:过滤无效")
})
public class SessionFilter implements Filter {

    private FilterConfig filterConfig;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void destroy() {
        this.filterConfig = null;
    }

    public static boolean isContains(String container, String[] regx) {
        boolean result = false;

        for (int i = 0; i < regx.length; i++) {
            if (container.indexOf(regx[i]) != -1) {//判断container参数中是否包含regx[i]这个参数
                return true;
            }
        }
        return false;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        //该类可以对后台返回的 response 在返回到前台前进行处理。
        // + 比如对返回的json串进行加密返回
        HttpServletResponseWrapper httpServletResponseWrapper = new HttpServletResponseWrapper((HttpServletResponse) servletResponse);

        //登录时的请求页面
        String logonStrings = filterConfig.getInitParameter("logonStrings");
        //过滤资源的后缀参数
        String includeStrings = filterConfig.getInitParameter("includeStrings");
        //没有登录的用户的转向页面
        String redirectPath = filterConfig.getInitParameter("redirectPath");
        //过滤器是否有效
        String disabletestfilter = filterConfig.getInitParameter("disabletestfilter");

        /*
        *   注意下面的if语句的判断顺序。
        *   1.判断当前过滤器是否失效
        *   2.粗粒度：通过请求后缀来划分需要过滤的请求
        *   3.细粒度：对登录页面放行
        *   4.最后对session范围的 useronly 变量进行判断。看是否是已登录用户
        * */

        //1.判断该过滤器是否有效，如果无效结束后序判断。Y为该过滤器无效
        if (disabletestfilter.toUpperCase().equals("Y")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // project/index.jsp ; login.do
        String[] loginList = logonStrings.split(";");
        // .do ; .jsp
        String[] includeList = includeStrings.split(";");

        //2.只对指定过滤参数后缀进行过滤 includeList中的内容 RequestURI包含不放行
        if (!this.isContains(httpServletRequest.getRequestURI(), includeList)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        //3.对登录页面不进行过滤
        if (this.isContains(httpServletRequest.getRequestURI(), loginList)) {
            //登录页面的请求放行
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        //4.判断用户是否登录
        String user = (String) httpServletRequest.getSession().getAttribute("useronly");
        if (user == null) {
            httpServletResponseWrapper.sendRedirect(redirectPath);//没登录跳回主页面
            return;
        } else {
            filterChain.doFilter(servletRequest, servletResponse);//登录放行
            return;
        }

    }
}
