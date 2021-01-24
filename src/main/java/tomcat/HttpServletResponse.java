package tomcat;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author yousj
 * @since 2020/12/11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpServletResponse {

    private Object content;

    private OutputStream out;

    public void write(Object data) throws IOException {
        JSONObject j = new JSONObject();
        j.put("code", 200);
        j.put("message", "success");
        j.put("data", data);
        out.write(("HTTP/1.1 200 \r\n" +
                "Content-Type:application/json;charset=utf-8;" +
                "Content-Length:" + data.toString().getBytes().length + " \r\n\r\n" +
                j).getBytes());
    }

    public void write(int code, String message, Object data) throws IOException {
        JSONObject j = new JSONObject();
        j.put("code", code);
        j.put("message", message);
        j.put("data", data);
        out.write(("HTTP/1.1 " + code + " \r\n" +
                "Content-Type:application/json;charset=utf-8;" +
                "Content-Length:" + data.toString().getBytes().length + " \r\n\r\n" +
                j).getBytes());
    }

    public static byte[] error404() {
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
        return ("HTTP/1.1 404 \r\n" +
                "Content-Type:text/html;charset=utf-8;" +
                "Content-Length:" + errorHtml.getBytes().length + " \r\n\r\n" +
                errorHtml).getBytes();
    }
}
