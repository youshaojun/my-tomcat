package tomcat.filter;

import tomcat.HttpServletRequest;
import tomcat.HttpServletResponse;

/**
 * @author yousj
 * @since 2020/12/12
 */
public interface Filter {

    /**
     * 初始化
     */
    void init(FilterConfig filterConfig) throws Exception;

    /**
     * 过滤
     */
    void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws Exception;

    /**
     * 销毁
     */
    void destroy() throws Exception;

    /**
     * 排序
     */
    int order();
}
