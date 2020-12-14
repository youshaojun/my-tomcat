package tomcat;

/**
 * @author yousj
 * @since 2020/12/11
 */
public interface Servlet {

    void init() throws Exception;

    void service(HttpServletRequest request, HttpServletResponse response) throws Exception;

}
