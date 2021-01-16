package person.shw.springboot.plugin.gateway.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.common.utils.Md5Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import person.shw.springboot.plugin.gateway.bean.APIApplication;
import person.shw.springboot.plugin.gateway.bean.APIService;
import person.shw.springboot.plugin.gateway.cache.GatewayAPICache;
import person.shw.springboot.plugin.gateway.util.NacosUtils;

import java.util.HashMap;
import java.util.Map;

import static com.alibaba.fastjson.serializer.SerializerFeature.*;
import static com.alibaba.fastjson.serializer.SerializerFeature.DisableCircularReferenceDetect;

/**
 * @author shihaowei
 * @date 2020/7/30 5:17 下午
 */
public class GatewayAPIAutoConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(GatewayAPIAutoConfiguration.class);

    private static final String NACOS_GROUP = "DEFAULT_GROUP";

    private static final SerializerFeature[] DEFALUT_SERIALIZER_FEATURE = new SerializerFeature[] {
            WriteMapNullValue,
            QuoteFieldNames,
            SkipTransientField,
            WriteEnumUsingToString,
            WriteDateUseDateFormat,
            DisableCircularReferenceDetect
    };

    @NacosInjected(properties = @NacosProperties(serverAddr = "47.105.165.0:8848",namespace = "${namespace:}"))
    private ConfigService configService;


    @Bean
    public CommandLineRunner apiConfigReportRunner() {
        return args -> {
            if (configService == null) {
                throw new NacosException(500, "[GatewayAPI] failed to inject configService");
            }
            String serverStatus = configService.getServerStatus();
            if (!"up".equalsIgnoreCase(serverStatus)) {
                throw new NacosException(500, "[GatewayAPI] failed to public gatewayAPI service (reason: nacos down)");
            }
            int size = GatewayAPICache.SERVICES.size();
            LOG.info("[GatewayAPI] found {} gateway api", size);
            if (size == 0) {
                return;
            }
            Map<String, String> serviceDataIdMap = new HashMap<>();
            for (APIService service : GatewayAPICache.SERVICES) {
                String serviceJson = JSON.toJSONString(service, DEFALUT_SERIALIZER_FEATURE);
                String serviceMd5 = Md5Utils.getMD5(serviceJson, "UTF-8");
                String serviceDataId = NacosUtils.getNacosDataId(service);
                // 推送接口信息到nacos
                boolean isOk = configService.publishConfig(serviceDataId, NACOS_GROUP, serviceJson);
                if (isOk) {
                    LOG.info("[GatewayAPI] success to public gatewayAPI service (dataId={})", serviceDataId);
                } else {
                    LOG.error("[GatewayAPI] failed to public gatewayAPI service (dataId={})", serviceDataId);
                }

                serviceDataIdMap.put(serviceDataId,serviceMd5);
            }
            
            APIApplication apiApplication = new APIApplication();
            apiApplication.setAppId(GatewayAPICache.APP_ID);
            apiApplication.setRoute(GatewayAPICache.ROUTE);
            apiApplication.setServiceDataIdMap(serviceDataIdMap);
            String appDataId = NacosUtils.getNacosDataId(apiApplication);
            boolean isOk = configService.publishConfig(appDataId, NACOS_GROUP, JSON.toJSONString(apiApplication, DEFALUT_SERIALIZER_FEATURE));
            if (isOk) {
                LOG.info("[GatewayAPI] success to public gatewayAPI application (dataId={})", appDataId);
            } else {
                LOG.error("[GatewayAPI] failed to public gatewayAPI application (dataId={})", appDataId);
            }

        };
    }

}
/* 这里针对每一条接口的配置所包含的方法以及内容

   Data ID : gatewayAPI:monkey-app:cn.com.supermonkey.app.module.common.api.RpcAppCommonApi::1.0
   配置内容 :
   {
	"methods": [{
		"returnType": "cn.com.supermonkey.dto.ResultDTO",
		"path": "/common/path",
		"methodName": "getMiniAppPath",
		"requestMethod": "POST",
		"paramNames": ["request"],
		"timeout": null,
		"retries": null,
		"paramTypes": ["cn.com.supermonkey.dto.RequestDTO"]
	}, {
		"returnType": "cn.com.supermonkey.dto.ResultDTO",
		"path": "/common/init",
		"methodName": "checkAppVersion",
		"requestMethod": "POST",
		"paramNames": ["request"],
		"timeout": null,
		"retries": null,
		"paramTypes": ["cn.com.supermonkey.dto.RequestDTO"]
	}],
	"serviceName": "cn.com.supermonkey.app.module.common.api.RpcAppCommonApi",
	"timeout": null,
	"appId": "monkey-app",
	"retries": 2,
	"serviceGroup": "",
	"serviceVersion": "1.0"
}
*/

/* 这里是针对app整个项目接口的集合

   Data ID : gatewayAPI:monkey-app:--api--app--v1
   配置内容 :
  {
        "route": "/api/app/v1",
        "appId": "monkey-app",
        "serviceDataIdMap": {
        "gatewayAPI:monkey-app:cn.com.supermonkey.app.module.test.api.RpcTestApi::1.0": "2b9fc370cd4523d6a01633fbf3a92cbd",
        "gatewayAPI:monkey-app:cn.com.supermonkey.app.module.user.api.RpcAppUserApi::1.0": "3d9189cba25c4908eadb093dfb123286",
        "gatewayAPI:monkey-app:cn.com.supermonkey.app.module.comment.api.RpcMessageCommentApi::1.0": "cc618b226ddf70e0f3a7d8ec75f70502",
        "gatewayAPI:monkey-app:cn.com.supermonkey.app.module.user.api.RpcUserLoginApi::1.0": "0974d7708723022ae97c48f09baf71a7",
        "gatewayAPI:monkey-app:cn.com.supermonkey.app.module.comment.api.RpcArticleCommentApi::1.0": "bff00d0aa01abe5122d5b12213b7dbbd",
        "gatewayAPI:monkey-app:cn.com.supermonkey.app.module.file.api.RpcFileApi::1.0": "7d78c83b6e70ce2ffe6d164a5118ce35",
        "gatewayAPI:monkey-app:cn.com.supermonkey.app.module.user.api.RpcMessageApi::1.0": "04a121e8e02a6ca90b6f5ce9f7537f2b",
        "gatewayAPI:monkey-app:cn.com.supermonkey.app.module.media.api.RpcResSoundApi::1.0": "17f04adf00a2cb905ef72f7f8425179c",
        "gatewayAPI:monkey-app:cn.com.supermonkey.app.module.zone.api.ZoneArticleFeaturedApi::1.0": "b95cf6f3124761f8c9f62b3ac439d3d1",
        "gatewayAPI:monkey-app:cn.com.supermonkey.app.module.user.api.RpcUserBindApi::1.0": "8ec5954967b4704207e8c425a3e92021"
        }
   }
*/

/** dubbonacos自己会推送配置信息到配置中心，不过一般都不满足场景的使用 */