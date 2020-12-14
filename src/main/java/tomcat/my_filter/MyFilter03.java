package tomcat.my_filter;

import tomcat.HttpServletRequest;
import tomcat.HttpServletResponse;
import tomcat.filter.Filter;
import tomcat.filter.FilterChain;
import tomcat.filter.FilterConfig;

/**
 * @author yousj
 * @since 2020/12/12
 */
public class MyFilter03 implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws Exception {
        System.out.println("MyFilter03 init......");
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws Exception {
        System.out.println("MyFilter03 load......");
        Thread.sleep(500);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("MyFilter03 destroy......");
    }

    @Override
    public int order() {
        return 1;
    }
}
