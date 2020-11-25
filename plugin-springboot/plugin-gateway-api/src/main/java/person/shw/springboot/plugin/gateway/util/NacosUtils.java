package person.shw.springboot.plugin.gateway.util;


import person.shw.springboot.plugin.gateway.bean.APIApplication;
import person.shw.springboot.plugin.gateway.bean.APIService;

/**
 * Created by wuweiqiang on 2020-04-26.
 */
public class NacosUtils {

    private static final String DATA_ID_PREFIX = "gatewayAPI";

    public static String getNacosDataId(APIApplication a) {
        return getNacosDataId(a.getAppId(), a.getRoute());
    }

    public static String getNacosDataId(String appId, String route) {
        return String.format("%s:%s:%s", DATA_ID_PREFIX, appId, route.replace("/", "--"));
    }

    public static String getNacosDataId(APIService s) {
        return String.format("%s:%s:%s:%s:%s", DATA_ID_PREFIX, s.getAppId(), s.getServiceName(), s.getServiceGroup(), s.getServiceVersion());
    }
}
