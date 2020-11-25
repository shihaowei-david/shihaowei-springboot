package person.shw.springboot.plugin.gateway.annotation;

import person.shw.springboot.plugin.gateway.constant.RequestMethod;

import java.lang.annotation.*;

/**
 * @author shihaowei
 * @date 2020/7/30 5:06 下午
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface GatewayAPI {

    /** 匹配路径(不含route路径) */
    String path() default "";

    /** 请求方式 */
    RequestMethod method() default RequestMethod.POST;

    /** 重试次数(不包含第一次) */
    int retries() default 0;

    /** 超时时间 */
    int timeout() default 0;
}
