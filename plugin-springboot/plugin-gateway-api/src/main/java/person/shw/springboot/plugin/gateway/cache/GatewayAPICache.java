package person.shw.springboot.plugin.gateway.cache;

import person.shw.springboot.plugin.gateway.bean.APIService;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author shihaowei
 * @date 2020/7/30 5:36 下午
 */
public class GatewayAPICache {

    public static final CopyOnWriteArrayList<APIService> SERVICES = new CopyOnWriteArrayList<>();
    public static String APP_ID = "";
    public static String ROUTE = "";

    public static void init(String appId,String route){
        APP_ID = appId;
        ROUTE = route;
    }

}
