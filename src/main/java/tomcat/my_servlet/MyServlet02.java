package tomcat.my_servlet;

import tomcat.HttpServlet;
import tomcat.HttpServletRequest;
import tomcat.HttpServletResponse;

/**
 * @author yousj
 * @since 2020/12/11
 */
public class MyServlet02 extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("doGet = " + request.getRequestParams());
        response.write("hello, your request myServlet02 doGet success.");
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("doPost = " + request.getRequestParams());
        response.write("hello, your request myServlet02 doPost success.");
    }
}
