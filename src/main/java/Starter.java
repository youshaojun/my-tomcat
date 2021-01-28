import tomcat.MyTomcat;

import static tomcat.MyTomcat.DISPATCHER_SERVLET_FLAG;

/**
 * 服务测试启动类
 *
 * @author yousj
 * @since 2020/12/11
 */
public class Starter {

    public static void main(String[] args) throws Exception {

        // 打开dispatcherServlet
        // DISPATCHER_SERVLET_FLAG = true;
        MyTomcat tomcat = new MyTomcat(true);
        tomcat.run();

    }

}
