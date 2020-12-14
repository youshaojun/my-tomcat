package tomcat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import tomcat.filter.Filter;
import tomcat.filter.FilterChainImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 服务器
 *
 * @author yousj
 * @since 2020/12/11
 */
public class MyTomcat {

    public static boolean DISPATCHER_SERVLET_FLAG = false;

    private static ExecutorService executor = Executors.newFixedThreadPool(50);

    private static ServerSocket socket;

    private int port;

    public MyTomcat(int port) {
        this.port = port;
    }

    /**
     * 启动服务
     */
    public void run() throws Exception {
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
        System.out.println("=============" + currentTime + " my tomcat start, bind port " + port + " ================");
        // 绑定端口, 获取socket
        socket = new ServerSocket(port);
        // 初始化HttpServlet
        new HttpServlet();
        while (true) {
            // 死循环获取连接
            Socket accept = socket.accept();
            //  BIO模式, 每连接对应每线程处理
            executor.execute(() -> handler(accept));
            Thread.sleep(10);
        }
    }

    public static void stop() {
        try {
            List<Filter> filters = FilterChainImpl.filters;
            if (filters != null) {
                for (Filter filter : filters) {
                    filter.destroy();
                }
            }
            socket.close();
            System.out.println("============ server stop success =============");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * 处理请求
     */
    private void handler(Socket socket) {
        InputStream in = null;
        OutputStream out = null;
        try {
            long l = System.currentTimeMillis();
            in = socket.getInputStream();
            out = socket.getOutputStream();
            byte[] bytes = new byte[1024];
            int len;
            while ((len = in.read(bytes)) != -1) {
                // 获取到请求数据
                String content = new String(bytes, 0, len, "utf-8");
                if (content.contains("HTTP/1.1")) {
                    // 处理http请求
                    execute(content, out);
                }
                System.out.println("请求处理时间 = " + (System.currentTimeMillis() - l) + "ms");
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 解码编码, 封装请求
     */
    private void execute(String content, OutputStream out) throws Exception {
        // 解析请求参数, 封装HttpServletRequest,HttpServletResponse
        HttpServletRequest request = new HttpServletRequest(null, "GET", null, content.split(" ")[1].split("\\?")[0]);
        if (content.contains("POST")) {
            request.setMethod("POST");
        }
        String path;
        HttpServlet httpServlet = new HttpServlet();
        String[] split = content.split(" ")[1].split("\\?");
        if (split.length > 1) {
            // 解析了url拼接的参数
            request.setRequestParams(Arrays.stream(URLDecoder.decode(split[1], "utf-8").split("&")).map(kv -> kv.split("=")).collect(Collectors.toMap(k -> k[0], v -> v[1])));
            // 解析body参数, 这里只处理了json格式
            String[] body = content.split("\r\n\r\n");
            if (body.length > 1) {
                try {
                    request.getRequestParams().putAll(JSONObject.parseObject(body[1]));
                } catch (Exception e) {
                    e.printStackTrace();
                    String result = "对不起,请检查请求参数是否为正确格式的json.";
                    out.write(("HTTP/1.1 400 \r\n" +
                            "Content-Type:application/json;charset=utf-8;" +
                            "Content-Length:" + result.getBytes().length + " \r\n\r\n" +
                            JSON.toJSONString(result)).getBytes());
                    return;
                }
            }
        }
        // dispatcherServlet 特殊处理, 拦截所有请求
        if (DISPATCHER_SERVLET_FLAG) {
            path = "dispatcherServlet";
        } else {
            path = content.split("/")[1].split("\\?")[0];
        }
        Object obj = HttpServlet.servlets.get(path);
        if (obj != null) {
            request.setObj(obj);
            httpServlet.service(request, new HttpServletResponse(null, out));
        } else {
            error404(out);
        }
    }

    public static void error404(OutputStream out) throws IOException {
        String errorHtml = "<div id=\"wrapper_wrapper\">\n" +
                "        <div id=\"content_left\">\n" +
                "            <div class=\"nors\">\n" +
                "                <div class=\"norsSuggest\">\n" +
                "                    <h1>ERROR 404!</h1>" +
                "                    <h3 class=\"norsTitle\">很抱歉，您要访问的页面不存在！</h3>\n" +
                "                    <p class=\"norsTitle2\">温馨提示：</p>\n" +
                "                    <ol>\n" +
                "                        <li>请检查您访问的网址是否正确</li>\n" +
                "                        <li>如果您不能确认访问的网址，请浏览<a href=\"//www.baidu.com/more/index.html\">百度更多</a>页面查看更多网址。</li>\n" +
                "                    </ol>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </div>";
        out.write(("HTTP/1.1 404 \r\n" +
                "Content-Type:text/html;charset=utf-8;" +
                "Content-Length:" + errorHtml.getBytes().length + " \r\n\r\n" +
                errorHtml).getBytes());
    }

}
