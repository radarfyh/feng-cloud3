package work.metanet.feng.auth.service;

import work.metanet.feng.common.core.constant.SecurityConstants;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.store.redis.JdkSerializationStrategy;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStoreSerializationStrategy;
import org.springframework.stereotype.Service;

/**
 * 授权码服务实现类
 * <p>
 * 该类用于管理授权码的存储和删除，支持集群环境。
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FengAuthorizationCodeServicesImpl extends RandomValueAuthorizationCodeServices {

    private static final String REDIS_KEY_PREFIX = SecurityConstants.FENG_PREFIX + SecurityConstants.OAUTH_CODE_PREFIX;

    /**
     * 授权码模式时: 验证码有效期设置, 默认有效期为5分钟, 单位秒
     */
    @Value("${feng.authorizationCode.expirationTime:300}")
    private Long expirationTime;

    private final RedisConnectionFactory connectionFactory;

    @Setter
    private RedisTokenStoreSerializationStrategy serializationStrategy = new JdkSerializationStrategy();

    /**
     * 保存授权码和认证信息到 Redis
     * <p>
     * 该方法用于将授权码和对应的认证信息存储到 Redis 中，并设置过期时间。
     * </p>
     *
     * @param code 授权码
     * @param authentication 认证信息
     */
    @Override
    protected void store(String code, OAuth2Authentication authentication) {
        try {
            @Cleanup
            RedisConnection connection = connectionFactory.getConnection();

            byte[] key = serializationStrategy.serialize(REDIS_KEY_PREFIX + code);
            byte[] value = serializationStrategy.serialize(authentication);

            connection.set(key, value, Expiration.seconds(expirationTime), RedisStringCommands.SetOption.UPSERT);

            log.debug("store --> key: {}, value: {}, expirationTime: {}", 
                    key, value, expirationTime);
        } catch (Exception e) {
            log.error("保存授权码和认证信息失败", e);
        }
    }

    /**
     * 从 Redis 中删除授权码并返回认证信息
     * <p>
     * 该方法用于从 Redis 中删除指定的授权码，并返回对应的认证信息。
     * </p>
     *
     * @param code 授权码
     * @return 认证信息
     */
    @Override
    protected OAuth2Authentication remove(String code) {
        try {
            @Cleanup
            RedisConnection connection = connectionFactory.getConnection();

            byte[] key = serializationStrategy.serialize(REDIS_KEY_PREFIX + code);
            byte[] value = connection.get(key);

            if (value == null) {
                return null;
            }

            OAuth2Authentication oAuth2Authentication = serializationStrategy.deserialize(value,
                    OAuth2Authentication.class);

            connection.del(key);

            log.debug("remove --> key: {}, value: {}", key, value);

            return oAuth2Authentication;
        } catch (Exception e) {
            log.error("删除授权码和获取认证信息失败", e);
            return null;
        }
    }
}