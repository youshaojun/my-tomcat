package tomcat.annotation;

import java.lang.annotation.*;

/**
 * @author yousj
 * @since 2020/12/14
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RequestMapping {
    String value() default "";
}
