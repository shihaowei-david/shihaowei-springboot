package person.shw.bootstrap;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.annotation.*;

/**
 * @author shihaowei
 * @date 2020/8/20 2:59 下午
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@SpringBootApplication(scanBasePackages = {
        "person.shw"
})
public @interface ShwBootApplication {
}
