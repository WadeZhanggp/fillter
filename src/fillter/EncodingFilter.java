package fillter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author Nuc YongGuang Ji
 * Created by JiYongGuang on 2017/5/2.
 */
@WebFilter(filterName = "EncodingFilter", urlPatterns = "/*", initParams = {
        @WebInitParam(name = "encoding", value = "UTF-8", description = "Specification Charset Encoding for all requests"),
        //true：无论request是否指定了字符集，都是用encoding。
        // false：如果request已指定一个字符集，则不使用encoding
        @WebInitParam(name = "forceEncoding", value = "false", description = "Variable Charset Encoding")
})
public class EncodingFilter implements Filter {

    private FilterConfig filterConfig;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        String encoding = filterConfig.getInitParameter("encoding");
        if (encoding == null) {
            encoding = "UTF-8";
        }

        httpServletRequest.setCharacterEncoding(encoding);
        System.out.println("httpServletRequest.setCharacterEncoding(encoding)");

        filterChain.doFilter(servletRequest,servletResponse);

        System.out.println("httpServletResponse.setCharacterEncoding(encoding)");
        httpServletResponse.setCharacterEncoding(encoding);
    }

    @Override
    public void destroy() {
        this.filterConfig = null;
    }
}
