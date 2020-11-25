package person.shw.springboot.plugin.gateway.scanner;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import person.shw.springboot.plugin.gateway.annotation.EnableGatewayAPI;
import person.shw.springboot.plugin.gateway.annotation.GatewayAPI;
import person.shw.springboot.plugin.gateway.bean.APIMethod;
import person.shw.springboot.plugin.gateway.bean.APIService;
import person.shw.springboot.plugin.gateway.cache.GatewayAPICache;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author shihaowei
 * @date 2020/7/30 5:17 下午
 */
public class GatewayAPIAnnotationScanner implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> cls = bean.getClass();
        if (cls.isAnnotationPresent(EnableGatewayAPI.class)){
            EnableGatewayAPI annotation = cls.getAnnotation(EnableGatewayAPI.class);
            GatewayAPICache.init(System.getProperty("appid"),annotation.route());
        }else if (cls.isAnnotationPresent(Service.class)){
            if (AopUtils.isAopProxy(bean)){
                cls = AopUtils.getTargetClass(bean);
            }
            Method[] methods = cls.getMethods();
            List<APIMethod> apiMethodList = new ArrayList<>();
            for (Method method : methods) {
                if(!method.isAnnotationPresent(GatewayAPI.class) || !Modifier.isPublic(method.getModifiers())){
                   continue;
                }
                Parameter[] parameters = method.getParameters();
                String[] paramNames = new String[parameters.length];
                String[] paramTypes = new String[parameters.length];

                // 遍历方法里面的参数，参数可能为多数
                for (int i = 0; i < parameters.length; i++) {
                    Parameter p = parameters[i];
                    paramNames[i] = p.getName();
                    paramTypes[i] = p.getParameterizedType().getTypeName();
                }

                GatewayAPI gatewayAPI = method.getAnnotation(GatewayAPI.class);

                APIMethod apiMethod = new APIMethod();
                apiMethod.setMethodName(method.getName());
                apiMethod.setPath(gatewayAPI.path());
                apiMethod.setRequestMethod(gatewayAPI.method().name());
                apiMethod.setReturnType(method.getReturnType().getTypeName());
                apiMethod.setParamTypes(paramTypes);
                apiMethod.setParamNames(paramNames);

                if (gatewayAPI.retries() > 0) {
                    apiMethod.setRetries(gatewayAPI.retries());
                }
                if (gatewayAPI.timeout() > 0) {
                    apiMethod.setTimeout(gatewayAPI.timeout());
                }

                apiMethodList.add(apiMethod);
            }

            if (apiMethodList.size() > 0){
                String appId = System.getProperty("appid");
                if (StringUtils.isBlank(appId)){
                    throw new NullPointerException("[GatewayAPI] ${app.id} can not be Null");
                }
                String service = cls.getInterfaces()[0].getName();
                if (StringUtils.isBlank(service)) {
                    throw new NullPointerException("[GatewayAPI] service name can not be null");
                }
                Service serviceAnnotation = cls.getAnnotation(Service.class);
                String serviceVersion = serviceAnnotation.version();
                if (StringUtils.isBlank(serviceVersion)) {
                    throw new NullPointerException("[GatewayAPI] service version can not be null");
                }
                String dubboGroup = serviceAnnotation.group();
                int retries = serviceAnnotation.retries();
                int timeout = serviceAnnotation.timeout();

                APIService apiInterface = new APIService();
                apiInterface.setAppId(appId);
                apiInterface.setServiceGroup(dubboGroup);
                apiInterface.setServiceName(service);
                apiInterface.setServiceVersion(serviceVersion);
                apiInterface.setMethods(apiMethodList);
                if (retries > 0) {
                    apiInterface.setRetries(retries);
                }
                if (timeout > 0) {
                    apiInterface.setTimeout(timeout);
                }

                //apiInterface.setMethodNames(apiMethodList.stream().map(c -> c.getName()).toArray(String[]::new));

                GatewayAPICache.SERVICES.add(apiInterface);
            }


        }


        return bean;
    }
}
