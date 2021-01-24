package tomcat.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import tomcat.HttpServlet;
import tomcat.HttpServletRequest;
import tomcat.HttpServletResponse;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static tomcat.MyTomcat.DISPATCHER_SERVLET_FLAG;

/**
 * BIO 服务器
 *
 * @author yousj
 * @since 2021/1/23
 */
public class BioServer {

    public static void bioServer(int port) throws Exception {
        // 初始化线程池
        ExecutorService executor = Executors.newFixedThreadPool(50);
        // 绑定端口, 获取socket
        ServerSocket server = new ServerSocket(port);
        while (true) {
            // 死循环获取连接, accept操作会阻塞
            Socket client = server.accept();
            // BIO模式, 每连接对应每线程处理
            executor.execute(() -> handlerBio(client));
            Thread.sleep(10);
        }
    }

    /**
     * 处理请求
     */
    private static void handlerBio(Socket socket) {
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
                System.out.println("content = " + content);
                if (content.contains("HTTP/1.1")) {
                    // 处理http请求
                    execute(content, out);
                    System.out.println("请求处理时间 = " + (System.currentTimeMillis() - l) + "ms");
                    break;// 长连接不需要break
                } else {
                    out.write("sorry, this request method is not supported".getBytes());
                    break;
                }
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
    private static void execute(String content, OutputStream out) throws Exception {
        // 解析请求参数, 封装HttpServletRequest,HttpServletResponse
        HttpServletRequest request = new HttpServletRequest(null, "GET", null, content.split(" ")[1].split("\\?")[0]);
        if (content.contains("POST")) {
            request.setMethod("POST");
        }
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
        String path;
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
            out.write(HttpServletResponse.error404());
        }
    }
}
