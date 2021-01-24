package tomcat.my_servlet;

import tomcat.HttpServlet;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author yousj
 * @since 2021/1/24
 */
public class MyNioTest extends HttpServlet {

    public String getCurrentDate() {
        return "当期服务器时间: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

}
