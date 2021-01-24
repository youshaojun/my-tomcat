package tomcat.my_servlet;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import tomcat.HttpServlet;
import tomcat.HttpServletRequest;
import tomcat.HttpServletResponse;
import tomcat.MyTomcat;
import tomcat.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author yousj
 * @since 2020/12/12
 */
public class DispatcherServlet extends HttpServlet {

    // 请求url与类和方法映射容器
    private static Map<String, Map<String, Object>> requestMappings = null;

    @Override
    public void init() {
        try {
            // 初始化请求url与类和方法映射容器
            initRequestMappings();
        } catch (Exception e) {
            System.err.println("initRequestMappings error. ");
            e.printStackTrace();
            MyTomcat.stop();
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String url = request.getUrl();
        Map<String, Object> map = requestMappings.get(url);
        if (map == null) {
            response.getOut().write(HttpServletResponse.error404());
        } else {
            Object clazz = map.get("clazz");
            // 通过Java反射执行相应的方法
            response.write(clazz.getClass().getMethod((String) map.get("method"), Map.class).invoke(clazz, request.getRequestParams()));
        }
    }

    private void initRequestMappings() throws Exception {
        if (requestMappings == null) {
            requestMappings = new HashMap<>();
            Reflections reflections = new Reflections(new ConfigurationBuilder().addUrls(ClasspathHelper.forPackage("tomcat")).addScanners(new MethodAnnotationsScanner()));
            Set<Method> methodsAnnotatedWith = reflections.getMethodsAnnotatedWith(RequestMapping.class);
            for (Method method : methodsAnnotatedWith) {
                String path = "";
                RequestMapping parent = method.getDeclaringClass().getAnnotation(RequestMapping.class);
                if (parent != null) {
                    path += parent.value();
                }
                path += method.getAnnotation(RequestMapping.class).value();
                if (requestMappings.containsKey(path)) {
                    throw new Exception("sorry, this path(" + path + ") is exist.");
                }
                Map<String, Object> map = new HashMap<>();
                map.put("clazz", method.getDeclaringClass().newInstance());
                map.put("method", method.getName());
                requestMappings.put(path, map);
            }
            System.out.println(requestMappings);
        }
    }
}
