package tomcat;

import org.reflections.Reflections;
import tomcat.filter.FilterChainImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yousj
 * @since 2020/12/11
 */
public class HttpServlet implements Servlet {

    // Servlet容器
    public static Map<String, Object> servlets = null;

    public HttpServlet() {
        // 初始化Servlet容器
         init();
    }

    @Override
    public void init() {
        if (servlets == null) {
            servlets = new HashMap<>();
            // serviceName为首字母小写类名
            new Reflections("tomcat").getSubTypesOf(HttpServlet.class).forEach(servlet -> {
                try {
                    String simpleName = servlet.getSimpleName();
                    servlets.put((simpleName.charAt(0) + "").toLowerCase() + simpleName.substring(1, simpleName.length()), servlet.newInstance());
                } catch (Exception e) {
                    System.err.println("init servlet error.");
                    e.printStackTrace();
                    MyTomcat.stop();
                }
            });
            System.out.println(servlets);
            System.out.println("=============== servlets load success ================");
        }
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 执行过滤器
        new FilterChainImpl().doFilter(request, response);
        String requestMethod = request.getMethod();
        // 处理请求
        if ("POST".equals(requestMethod)) {
            ((HttpServlet) request.getObj()).doPost(request, response);
        } else {
            ((HttpServlet) request.getObj()).doGet(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {

    }

}
