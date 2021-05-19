package Gateway.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @title GatewayUtil.java
 * @description 网关工具类
 * @author wwc
 * @date 2020/12/02 17:11
 * @version 1.0
 */
@Component
@Slf4j
public class GatewayUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 供AuthFilter使用的工具
     */
    public static RedisTemplate redis;

    public static Map<String, Integer> gatewayPrivilegeList;

    private final static Integer jwtExpireTime = 3600;

    private final static Integer refreshJwtTime = 60;

    @PostConstruct
    public void getRedisTemplate(){
        redis = this.redisTemplate;
        log.info("初始化-------redisTemplate----");
        gatewayPrivilegeList = this.redisTemplate.opsForHash().entries("Privilege");
        log.info("初始化-------gatewayPrivilegeList----");
    }

    public static String getUrlPrivilegeByKey(String urlKeyName) {
        Set<String> allKeySet = gatewayPrivilegeList.keySet();
        if (allKeySet.contains(urlKeyName)) {
            return gatewayPrivilegeList.get(urlKeyName).toString();
        } else {
            return null;
        }
    }

    public static Integer getJwtExpireTime() {
        return jwtExpireTime;
    }

    public static Integer getRefreshJwtTime() {
        return refreshJwtTime;
    }

    /**
     * 请求类型
     */
    public enum RequestType {
        GET(0),
        POST(1),
        PUT(2),
        DELETE(3);

        private final int code;

        RequestType(int code) {
            this.code = code;
        }

        public static RequestType getCodeByType(HttpMethod method) {
            return switch (method) {
                case GET -> RequestType.GET;
                case PUT -> RequestType.PUT;
                case POST -> RequestType.POST;
                case DELETE -> RequestType.DELETE;
                default -> null;
            };
        }

        public Integer getCode() {
            return code;
        }

    }
}
