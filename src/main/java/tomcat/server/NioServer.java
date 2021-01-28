package tomcat.server;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import tomcat.HttpServlet;
import tomcat.HttpServletResponse;
import tomcat.MyTomcat;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * NIO 服务器
 *
 * @author yousj
 * @since 2021/1/23
 */
public class NioServer {

    private static Selector selector = null;

    public static void nioServer(int port) throws Exception {
        // 初始化socket
        ServerSocketChannel ssc = ServerSocketChannel.open();
        // 绑定端口
        ssc.bind(new InetSocketAddress(port));
        // 设置非阻塞
        ssc.configureBlocking(false);
        // 初始化多路复用器
        selector = Selector.open();
        // 注册多路复用器, 监听accept操作
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            // 轮询多路复用器
            while (selector.select(10) > 0) {
                // 获取多路复用器返回的有效的SelectionKeys迭代器
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey sk = it.next();
                    // 不会自动删除, 手动删除SelectionKey
                    it.remove();
                    if (sk.isAcceptable()) {
                        // 连接操作
                        acceptHandler(sk);
                    } else if (sk.isReadable()) {
                        // 读操作
                        readHandler(sk);
                    } else if (sk.isWritable()) {
                        // 写操作
                        writeHandler(sk);
                    }
                }
            }
        }
    }


    private static void acceptHandler(SelectionKey key) throws Exception {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        // 获取客户端连接
        SocketChannel client = ssc.accept();
        // 设置非阻塞
        client.configureBlocking(false);
        // 分配ByteBuffer缓冲区
        ByteBuffer buffer = ByteBuffer.allocateDirect(4096);
        //  注册多路复用器, 监听read操作, 并分配buffer
        client.register(selector, SelectionKey.OP_READ, buffer);
    }

    private static void readHandler(SelectionKey key) throws Exception {
        // 获取SelectionKey上的channel
        SocketChannel sc = (SocketChannel) key.channel();
        // 获取分配的buffer
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        // 清空buffer
        buffer.clear();
        while (true) {
            int len;
            String content = "";
            // 读取请求数据
            while ((len = sc.read(buffer)) > 0) {
                // 翻转buffer, 指的是翻转数据读写操作的指针
                buffer.flip();
                while (buffer.hasRemaining()) {
                    byte[] bytes = new byte[buffer.limit()];
                    // 取出buffer中的数据
                    buffer.get(bytes);
                    content = new String(bytes);
                }
            }
            // 等于-1手动关闭通道, 否则会出现空轮训
            if (len < 0) {
                sc.close();
            }
            buffer.clear();
            sc.configureBlocking(false);
            // 处理Http请求
            if (content.contains("HTTP/1.1")) {
                Object obj = HttpServlet.servlets.get(content.split("/")[1]);
                if (obj != null) {
                    try {
                        Object data = obj.getClass().getMethod(content.contains("?") ? content.split("/")[2].split("\\?")[0] : content.split("/")[2].split(" ")[0]).invoke(obj);
                        buffer.put(response(200, "success", data));
                    } catch (Exception e) {
                        buffer.put(response(500, "无效请求.", e));
                    }
                } else {
                    buffer.put(HttpServletResponse.error404());
                }
                // 注册多路复用器, 监听write操作, 并分配buffer
                sc.register(selector, SelectionKey.OP_WRITE, buffer);
                break;
            }
            String response = "^-^";
            if (StrUtil.isNotBlank(content)) {
                response = JSONObject.parseObject(HttpUtil.get(MyTomcat.ROBOT_URL + content)).getString("content") + "\n";
            }
            buffer.put(response.getBytes());
            sc.write(buffer);
            break;
        }
    }

    private static void writeHandler(SelectionKey key) throws Exception {
        // 获取SelectionKey上的channel
        SocketChannel sc = (SocketChannel) key.channel();
        // 获取分配的buffer
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        // 翻转
        buffer.flip();
        // 写服务器响应数据
        sc.write(buffer);
        buffer.clear();
        // 关闭连接
        sc.close();
    }


    private static byte[] response(int code, String message, Object data) {
        JSONObject j = new JSONObject();
        j.put("code", code);
        j.put("message", message);
        j.put("data", data);
        return ("HTTP/1.1 " + code + " \r\n" +
                "Content-Type:application/json;charset=utf-8;" +
                "Content-Length:" + data.toString().getBytes().length + " \r\n\r\n" +
                j).getBytes();
    }
}
