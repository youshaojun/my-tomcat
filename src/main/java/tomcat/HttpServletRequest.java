package tomcat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author yousj
 * @since 2020/12/11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HttpServletRequest {

    private Map<String, Object> requestParams;

    private String method;

    private Object obj;

    private String url;

}
