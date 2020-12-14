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
}
