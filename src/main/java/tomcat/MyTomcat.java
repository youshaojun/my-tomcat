package tomcat;

import tomcat.filter.Filter;
import tomcat.filter.FilterChainImpl;
import tomcat.server.BioServer;
import tomcat.server.NioServer;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 服务器
 *
 * @author yousj
 * @since 2020/12/11
 */
public class MyTomcat {

    public static boolean DISPATCHER_SERVLET_FLAG = false;

    public static final String ROBOT_URL = "http://api.qingyunke.com/api.php?key=free&appid=0&msg=";

    private int port = 8080;

    private boolean isNio = false;

    public MyTomcat() {
    }

    public MyTomcat(int port) {
        this.port = port;
    }

    public MyTomcat(boolean isNio) {
        this.isNio = isNio;
    }

    public MyTomcat(int port, boolean isNio) {
        this.port = port;
        this.isNio = isNio;
    }

    /**
     * 启动服务
     */
    public void run() throws Exception {
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
        System.out.println("=============" + currentTime + " my tomcat start, bind port " + port + " ================");
        // 初始化servlet
        new HttpServlet();
        // 启动服务
        if (isNio) NioServer.nioServer(port);
        else BioServer.bioServer(port);
    }

    /**
     * 停止服务
     */
    public static void stop() {
        try {
            List<Filter> filters = FilterChainImpl.filters;
            if (filters != null) {
                for (Filter filter : filters) {
                    filter.destroy();
                }
            }
            System.out.println("============ server stop success =============");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(1);
    }

}
