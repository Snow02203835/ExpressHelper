package Gateway.localfilter;

import Gateway.util.GatewayUtil;
import Gateway.util.JwtHelper;
import Gateway.util.ResponseCode;
import com.alibaba.fastjson.JSONObject;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Snow created in 2021/05/18 20:05
 **/
public class AuthFilter implements GatewayFilter, Ordered {
    private  static  final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    private final String tokenName;

    public AuthFilter(Config config){
        this.tokenName = config.getTokenName();
    }

    /**
     * 权限过滤器
     * 1. 检查JWT是否合法,以及是否过期，如果过期则需要在response的头里换发新JWT，如果不过期将旧的JWT在response的头中返回
     * 2. 判断用户的shopId是否与路径上的shopId一致（0可以不做这一检查）
     * 3. 在redis中判断用户是否有权限访问url,如果不在redis中需要通过dubbo接口load用户权限
     * @author snow create 2021/05/19 09:00
     * @param exchange 源请求
     * @param chain 转发请求
     * @return Mono
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
        // 获取请求参数
        String token = request.getHeaders().getFirst(tokenName);
        RequestPath url = request.getPath();
        HttpMethod method = request.getMethod();

        // 判断token是否为空，无需token的url在配置文件中设置
        if (StringUtil.isNullOrEmpty(token)){
            logger.debug("Token is null or empty!");
            return returnResponse(ResponseCode.AUTH_NEED_LOGIN, HttpStatus.UNAUTHORIZED, response);
        }
        logger.debug("Token is not null: " + token);

        // 判断token是否合法
        JwtHelper.UserAndDepart userAndDepart = new JwtHelper().verifyTokenAndGetClaims(token);
        if (userAndDepart == null) {
            // 若token解析不合法
            logger.debug("Token is illegal!");
            return returnResponse(ResponseCode.AUTH_INVALID_JWT, HttpStatus.UNAUTHORIZED, response);
        }

        // 若token合法
        // 获取redis工具
        RedisTemplate redisTemplate = GatewayUtil.redis;
        // 判断该token是否被禁止
        String[] banSetName = {"BanJwt_0", "BanJwt_1"};
        for (String singleBanSetName : banSetName) {
            // 若redis有该banSetName键则检查
            if (redisTemplate.hasKey(singleBanSetName)) {
                // 获取全部被ban的jwt,若banJwt中有该token则拦截该请求
                if (redisTemplate.opsForSet().isMember(singleBanSetName, token)) {
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return response.writeWith(Mono.empty());
                }
            }
        }

        // 解析userid和departId和有效期
        Long userId = userAndDepart.getUserId();
        Long departId = userAndDepart.getDepartId();
        Date expireTime = userAndDepart.getExpTime();

        String newToken = token;
        // 判断该token有效期是否还长，load用户权限需要传token，将要过期的旧的token暂未放入banJwt中，有重复登录的问题
        long sec = expireTime.getTime() - System.currentTimeMillis();
        if (sec < GatewayUtil.getRefreshJwtTime() * 1000) {
            // 若快要过期了则重新换发token
            JwtHelper jwtHelper = new JwtHelper();
            newToken = jwtHelper.createToken(userId, departId, GatewayUtil.getJwtExpireTime());
            logger.debug("重新换发token:" + newToken);
        }

        // 判断redis中是否存在该用户的token，若不存在则重新load用户的权限
        String key = "up_" + departId;
        // 将token放在返回消息头中
        response.getHeaders().set(tokenName, newToken);
        // 将url中的数字替换成{id}
        Pattern p = Pattern.compile("(0|[1-9][0-9]*)");
        Matcher matcher = p.matcher(url.toString());
        String commonUrl = matcher.replaceAll("{id}");
        logger.debug("获取通用请求路径:" + commonUrl);
        // 找到该url所需要的权限id
        String urlKey = commonUrl + "-" + GatewayUtil.RequestType.getCodeByType(method).getCode().toString();
        String privilegeKey = GatewayUtil.getUrlPrivilegeByKey(urlKey);
        if (privilegeKey == null || privilegeKey.isEmpty()) {
            // 若该url无对应权限id
            logger.debug("该url无权限id:" + urlKey);
            return chain.filter(exchange);
        }
        // 拿到该用户的权限位,检验是否具有访问该url的权限
        if (redisTemplate.opsForSet().isMember(key, Integer.valueOf(privilegeKey))) {
            return chain.filter(exchange);
        }
        // 若全部检查完则无该url权限
        logger.debug("用户id = " + userId + ", departId = " + departId + " 无权限访问该URL");
        return  returnResponse(ResponseCode.URL_OUT_SCOPE, HttpStatus.FORBIDDEN, response);
    }

    private Mono<Void> returnResponse(ResponseCode code, HttpStatus status, ServerHttpResponse response){
        JSONObject message = new JSONObject();
        message.put("errno", code.getCode());
        message.put("errmsg", code.getMessage());
        byte[] bits = message.toJSONString().getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        response.setStatusCode(status);
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    public static class Config {
        private String tokenName;

        public Config(){
        }

        public String getTokenName() {
            return tokenName;
        }

        public void setTokenName(String tokenName) {
            this.tokenName = tokenName;
        }
    }
}
