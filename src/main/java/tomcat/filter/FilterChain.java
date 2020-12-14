package tomcat.filter;

import tomcat.HttpServletRequest;
import tomcat.HttpServletResponse;

/**
 * @author yousj
 * @since 2020/12/12
 */
public interface FilterChain {

    void doFilter(HttpServletRequest request, HttpServletResponse response) throws Exception;

}
