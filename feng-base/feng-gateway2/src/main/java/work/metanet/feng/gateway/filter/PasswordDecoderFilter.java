package work.metanet.feng.gateway.filter;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import work.metanet.feng.common.core.constant.CacheConstants;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.core.constant.SecurityConstants;
import work.metanet.feng.common.core.constant.enums.EncFlagTypeEnum;
import work.metanet.feng.common.core.util.WebUtils;
import work.metanet.feng.gateway.config.GatewayConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * PasswordDecoderFilter 用于处理密码解密的网关过滤器。
 * <p>
 * 该过滤器用于在 OAuth2 登录请求中对密码字段进行解密。解密过程通过判断请求的客户端类型来确定是否需要进行解密，
 * 并且根据配置从 Redis 获取相关的解密密钥和标识信息。支持对请求体进行解密处理。
 * </p>
 * <p>
 * 主要流程包括：判断请求是否是登录请求，判断是否需要解密，进行 AES 解密，最终修改请求体并继续执行。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
@SuppressWarnings("all")
public class PasswordDecoderFilter extends AbstractGatewayFilterFactory {

    // 常量定义，表示密码字段的名称和AES加密算法
    private static final String PASSWORD = "password";
    private static final String KEY_ALGORITHM = "AES";

    // 依赖注入，RedisTemplate 用于访问 Redis 配置
    private final RedisTemplate redisTemplate;
    private final GatewayConfigProperties gatewayConfig;

    // 默认的消息读者，用于处理请求体
    private final List<HttpMessageReader<?>> messageReaders = HandlerStrategies.withDefaults().messageReaders();

    /**
     * 过滤器的主要应用方法，用于处理每个请求。
     * 1. 判断是否是登录请求，不是则直接跳过处理。
     * 2. 判断是否需要解密，如果需要则进行密码解密处理。
     * 
     * @param config 配置参数
     * @return GatewayFilter 过滤器
     */
    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 1. 如果不是登录请求，直接继续处理
            if (!StrUtil.containsAnyIgnoreCase(request.getURI().getPath(), SecurityConstants.OAUTH_TOKEN_URL)) {
                return chain.filter(exchange);
            }

            // 2. 如果是刷新token类型，直接继续处理
            String grantType = request.getQueryParams().getFirst("grant_type");
            if (StrUtil.equals(SecurityConstants.REFRESH_TOKEN, grantType)) {
                return chain.filter(exchange);
            }

            // 3. 判断客户端是否需要解密，若无需解密则直接跳过
            if (!isEncClient(request)) {
                return chain.filter(exchange);
            }

			// 4. 前端加密密文解密逻辑
			Class inClass = String.class;
			Class outClass = String.class;
			ServerRequest serverRequest = ServerRequest.create(exchange, messageReaders);
			
			//log.debug("apply-->serverRequest: {}", JSONUtil.toJsonStr(serverRequest));
			
            Mono<?> modifiedBody = serverRequest.bodyToMono(inClass).flatMap(decryptAES());
            
            log.debug("apply-->modifiedBody: {}", JSONUtil.toJsonStr(modifiedBody));

            BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, outClass);
            HttpHeaders headers = new HttpHeaders();
            headers.putAll(exchange.getRequest().getHeaders());
            headers.remove(HttpHeaders.CONTENT_LENGTH);

            headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
            CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);

            return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {
                ServerHttpRequest decorator = decorate(exchange, headers, outputMessage);
                return chain.filter(exchange.mutate().request(decorator).build());
            }));
        };
    }

    /**
     * 判断请求的客户端是否需要加密传输。
     * 根据请求中的 clientId 从 Redis 获取配置，决定是否需要解密。
     * 
     * @param request 请求上下文
     * @return true 如果需要加密传输，false 如果是明文传输
     */
    private boolean isEncClient(ServerHttpRequest request) {
        String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String clientId = WebUtils.extractClientId(header).orElse(null);

        String organCode = request.getHeaders().getFirst(CommonConstants.ORGAN_CODE);
        String key = String.format("%s:%s", CacheConstants.CLIENT_FLAG, clientId);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        Object val = redisTemplate.opsForValue().get(key);

        // 如果配置不存在，默认需要解密
        if (val == null) {
            return true;
        }

        // 从Redis获取配置并检查是否需要解密
        JSONObject information = JSONUtil.parseObj(val.toString());
        return !StrUtil.equals(EncFlagTypeEnum.NO.getType(), information.getStr(CommonConstants.ENC_FLAG));
    }

    /**
     * 解密密码字段。
     * 解密请求体中的密码字段，并替换为解密后的密码。
     * 
     * @return 一个Function，用于解密密码
     */
    private Function<String, Mono<String>> decryptAES() {
        return s -> {
            try {
                // 获取前端加密传递的密钥和初始化向量（IV）
                String key = gatewayConfig.getEncodeKey();

                SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
                IvParameterSpec ivParameterSpec = new IvParameterSpec(key.getBytes(StandardCharsets.UTF_8)); // 使用与前端相同的密钥作为IV

                // 使用Hutool的AES类，设置相同的解密模式和填充方式
                AES aes = new AES(Mode.CFB, Padding.NoPadding, secretKeySpec, ivParameterSpec);

                // 解码请求的参数
                Map<String, String> inParamsMap = HttpUtil.decodeParamMap(s, CharsetUtil.CHARSET_UTF_8);

                log.debug("decryptAES-->inParamsMap: {}", JSONUtil.toJsonStr(inParamsMap));

                // 检查请求中是否包含需要解密的密码字段
                if (inParamsMap.containsKey(PASSWORD)) {
                    String encryptedPwd = inParamsMap.get(PASSWORD);
                    // 解密密码
                    String decryptedPwd = aes.decryptStr(encryptedPwd);
                    inParamsMap.put(PASSWORD, decryptedPwd); // 将解密后的密码替换回原字段
                    
                    // log.debug("decryptAES--> encryptedPwd: {}, decryptedPwd: {}", encryptedPwd, decryptedPwd);                    
                } else {
                    log.error("非法请求数据: {}", s);
                    throw new IllegalArgumentException("Missing password field in request body.");
                }

                // 返回修改后的请求参数，格式化为URL编码的字符串
                return Mono.just(HttpUtil.toParams(inParamsMap, Charset.defaultCharset(), true));
            } catch (Exception e) {
                log.error("解密密码失败: {}", e.getMessage(), e);
                return Mono.error(new IllegalArgumentException("解密失败"));
            }
        };
    }

    /**
     * 用于装饰 HTTP 请求，确保返回的请求体数据被正确修改。
     * 
     * @param exchange 请求交换对象
     * @param headers 修改后的请求头
     * @param outputMessage 包装了请求体的对象
     * @return 一个装饰后的请求对象
     */
    private ServerHttpRequestDecorator decorate(ServerWebExchange exchange, HttpHeaders headers, CachedBodyOutputMessage outputMessage) {
        return new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.putAll(super.getHeaders());
                long contentLength = headers.getContentLength();
                if (contentLength > 0) {
                    httpHeaders.setContentLength(contentLength);
                } else {
                    httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                }
                return httpHeaders;
            }

            @Override
            public Flux<DataBuffer> getBody() {
                return outputMessage.getBody();
            }
        };
    }
}

