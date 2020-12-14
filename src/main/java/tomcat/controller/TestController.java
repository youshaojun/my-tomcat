package tomcat.controller;

import tomcat.annotation.RequestMapping;

import java.util.Map;

/**
 * @author yousj
 * @since 2020/12/14
 */
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/testMapping")
    public Object test(Map<String, Object> params) {
        System.out.println("/test/testMapping=================>>>> params = " + params);
        return "/test/testMapping, ok";
    }

    @RequestMapping("/testMapping1")
    public Object test1(Map<String, Object> params) {
        System.out.println("/test/testMapping1==================>>>> params = " + params);
        return "/test/testMapping1 ,ok";
    }

}
