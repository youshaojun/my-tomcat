package tomcat.controller;

import tomcat.annotation.RequestMapping;

import java.util.Map;

/**
 * @author yousj
 * @since 2020/12/14
 */
public class TestController1 {

    @RequestMapping("/testMapping")
    public Object test(Map<String, Object> params) {
        System.out.println("testMapping=================>>>> params = " + params);
        return "testMapping, ok";
    }

    @RequestMapping("/testMapping1")
    public Object test1(Map<String, Object> params) {
        System.out.println("testMapping1=================>>>> params = " + params);
        return "testMapping1, ok";
    }

}
