package person.shw.springboot.plugin.gateway.annotation;

import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import org.springframework.context.annotation.Import;
import person.shw.springboot.plugin.gateway.config.GatewayAPIAutoConfiguration;
import person.shw.springboot.plugin.gateway.scanner.GatewayAPIAnnotationScanner;

import java.lang.annotation.*;

/**
 * @author shihaowei
 * @date 2020/7/30 5:08 下午
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import({GatewayAPIAutoConfiguration.class,
         GatewayAPIAnnotationScanner.class
})
@EnableNacosConfig
public @interface EnableGatewayAPI {

    String route() default  "";
}
