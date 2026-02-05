package work.metanet.feng.auth.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import work.metanet.feng.admin.api.entity.SysOauthClientDetails;
import work.metanet.feng.admin.api.feign.RemoteClientDetailsService;
import work.metanet.feng.common.core.constant.CacheConstants;
import work.metanet.feng.common.core.constant.SecurityConstants;
import work.metanet.feng.common.core.util.KeyStrResolver;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.core.util.RetOps;
import work.metanet.feng.common.core.util.SpringContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.ConvertingCursor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 令牌处理服务实现类
 * <p>
 * 该类用于处理与令牌相关的业务逻辑，包括删除令牌、查询令牌列表等。
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FengTokenDealServiceImpl {

    private static final String REDIS_KEY_PREFIX = SecurityConstants.FENG_PREFIX + SecurityConstants.OAUTH_PREFIX + "access:*";

    private final RedisTemplate redisTemplate;
    private final CacheManager cacheManager;
    private final TokenStore tokenStore;
    private final KeyStrResolver keyStrResolver;
    private final RemoteClientDetailsService clientDetailsService;

    /**
     * 删除请求令牌和刷新令牌
     * <p>
     * 该方法用于删除指定的请求令牌和刷新令牌，并清空相关缓存。
     * </p>
     *
     * @param token 请求令牌
     * @return 操作结果
     */
    public R<Boolean> removeToken(String token) {
        try {
            OAuth2AccessToken accessToken = tokenStore.readAccessToken(token);
            log.debug("removeToken --> token:{}, accessToken: {}", token, accessToken);

            if (accessToken == null || StrUtil.isBlank(accessToken.getValue())) {
                return R.ok(Boolean.TRUE, "退出失败，token 无效");
            }

            OAuth2Authentication auth2Authentication = tokenStore.readAuthentication(accessToken);
            // 清空用户信息
            cacheManager.getCache(CacheConstants.USER_DETAILS).evict(auth2Authentication.getName());
            log.debug("removeToken --> 清空用户信息: {}", auth2Authentication.getName());

            // 清空 access token
            tokenStore.removeAccessToken(accessToken);

            // 清空 refresh token
            OAuth2RefreshToken refreshToken = accessToken.getRefreshToken();
            tokenStore.removeRefreshToken(refreshToken);
            log.debug("removeToken --> 清空 refresh token: {}", refreshToken.toString());

            // 发送退出成功事件
            SpringContextHolder.publishEvent(new LogoutSuccessEvent(auth2Authentication));
            return R.ok();
        } catch (Exception e) {
            log.error("删除令牌失败", e);
            return R.failed("删除令牌失败");
        }
    }

    /**
     * 根据用户名查询令牌相关信息
     * <p>
     * 该方法用于根据用户名查询令牌列表，并返回分页结果。
     * </p>
     *
     * @param page 分页参数
     * @param username 用户名
     * @return 分页结果
     */
    public R queryTokenByUsername(Page page, String username) {
        List<OAuth2AccessToken> oAuth2AccessTokenList =
                RetOps.of(clientDetailsService.listClientDetails(SecurityConstants.FROM_IN))
                      .getData()
                      .orElseGet(Collections::emptyList)
                      .stream()
                      .map(SysOauthClientDetails::getClientId)
                      .flatMap(clientId -> tokenStore.findTokensByClientIdAndUserName(clientId, username).stream())
                      .distinct()
                      .collect(Collectors.toList());
        page.setRecords(oAuth2AccessTokenList);
        page.setTotal(oAuth2AccessTokenList.size());

        log.debug("queryTokenByUsername --> page: {}, username: {}", page.toString(), username);

        return R.ok(page);
    }

    /**
     * 分页查询令牌列表
     * <p>
     * 该方法用于从 Redis 中分页查询令牌列表。
     * </p>
     *
     * @param page 分页参数
     * @return 分页结果
     */
    public R<Page> queryToken(Page page) {
        try {
            // 根据分页参数获取对应数据
            String key = keyStrResolver.extract(REDIS_KEY_PREFIX, StrUtil.COLON);
            List<String> pages = findKeysForPage(key, page.getCurrent(), page.getSize());
            redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
            page.setRecords(redisTemplate.opsForValue().multiGet(pages));
            page.setTotal(redisTemplate.keys(key).size());

            log.debug("queryToken --> page:{}", page.toString());

            return R.ok(page);
        } catch (Exception e) {
            log.error("分页查询令牌列表失败", e);
            return R.failed("分页查询令牌列表失败");
        }
    }

    /**
     * 使用游标分页查询 Redis 键
     * <p>
     * 该方法用于从 Redis 中分页查询符合条件的键。
     * </p>
     *
     * @param patternKey 键的通配符
     * @param pageNum 当前页
     * @param pageSize 每页大小
     * @return 符合条件的键列表
     */
    private List<String> findKeysForPage(String patternKey, long pageNum, long pageSize) {
        ScanOptions options = ScanOptions.scanOptions().count(1000L).match(patternKey).build();
        RedisSerializer<String> redisSerializer = (RedisSerializer<String>) redisTemplate.getKeySerializer();
        Cursor cursor = (Cursor) redisTemplate.executeWithStickyConnection(
                redisConnection -> new ConvertingCursor<>(redisConnection.scan(options), redisSerializer::deserialize));
        List<String> result = new ArrayList<>();
        int tmpIndex = 0;
        long startIndex = (pageNum - 1) * pageSize;
        long end = pageNum * pageSize;

        assert cursor != null;
        while (cursor.hasNext()) {
            if (tmpIndex >= startIndex && tmpIndex < end) {
                result.add(cursor.next().toString());
                tmpIndex++;
                continue;
            }
            if (tmpIndex >= end) {
                break;
            }
            tmpIndex++;
            cursor.next();
        }

        try {
            cursor.close();
        } catch (Exception e) {
            log.error("关闭 cursor 失败", e);
        }
        log.debug("findKeysForPage --> patternKey: {}, result: {}", patternKey, result);

        return result;
    }

    /**
     * 查询令牌信息
     * <p>
     * 该方法用于查询指定令牌的详细信息。
     * </p>
     *
     * @param token 令牌
     * @return 令牌信息
     */
    public R queryTokenInfo(String token) {
        try {
            OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(token);
            log.debug("queryTokenInfo --> token: {}, oAuth2AccessToken: {}", token, oAuth2AccessToken.toString());
            return R.ok(oAuth2AccessToken);
        } catch (Exception e) {
            log.error("查询令牌信息失败", e);
            return R.failed("查询令牌信息失败");
        }
    }
}