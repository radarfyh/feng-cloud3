package work.metanet.feng.common.security.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import work.metanet.feng.admin.api.entity.SysOauthClientDetails;
import work.metanet.feng.admin.api.feign.RemoteClientDetailsService;
import work.metanet.feng.common.core.constant.CacheConstants;
import work.metanet.feng.common.core.constant.SecurityConstants;
import work.metanet.feng.common.core.util.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 该服务实现了 ClientDetailsService 接口，提供加载客户端详情的功能。
 * 通过缓存机制加速客户端信息的加载，并支持从远程服务获取客户端详情。
 */
@Slf4j
@Primary
@RequiredArgsConstructor
public class FengClientDetailsServiceImpl implements ClientDetailsService {

    private final RemoteClientDetailsService remoteClientDetailsService;

    /**
     * 通过客户端 ID 加载客户端详情，支持 Redis 缓存。
     * 
     * @param clientId 客户端 ID
     * @return ClientDetails 客户端详情
     * @throws InvalidClientException 客户端 ID 不存在或无效时抛出
     */
    @Override
    @Cacheable(value = CacheConstants.CLIENT_DETAILS_KEY, key = "#clientId", unless = "#result == null")
    public ClientDetails loadClientByClientId(String clientId) {
        R<SysOauthClientDetails> result = remoteClientDetailsService.getClientDetailsById(clientId, SecurityConstants.FROM_IN);
        SysOauthClientDetails clientDetails = result.getData();

        log.debug("loadClientByClientId --> clientId: {}, clientDetails: {}", clientId, JSONUtil.toJsonStr(clientDetails));

        if (clientDetails == null) {
            log.error("Client details not found for clientId: {}", clientId);
            throw new InvalidClientException("Client not found or invalid client ID");
        }

        // 适配成 OAuth2 内置的 ClientDetails 类型
        return clientDetailsWrapper(clientDetails);
    }

    /**
     * 将远程获取的客户端信息转换为 Spring OAuth2 需要的 ClientDetails 类型
     * 
     * @param origin 数据库中保存的客户端信息
     * @return 转换后的 ClientDetails
     */
    private ClientDetails clientDetailsWrapper(SysOauthClientDetails origin) {
        BaseClientDetails target = new BaseClientDetails();
        String clientId = origin.getClientId();
        String clientSecret = String.format("{noop}%s", origin.getClientSecret());
        log.debug("clientDetailsWrapper --> clientId: {}, clientSecret: {}", clientId, clientSecret);

        // 必选项
        target.setClientId(clientId);
        target.setClientSecret(clientSecret);
        
        // 设置授权方式
        if (ArrayUtil.isNotEmpty(origin.getAuthorizedGrantTypes())) {
            target.setAuthorizedGrantTypes(CollUtil.newArrayList(origin.getAuthorizedGrantTypes()));
        }

        // 设置权限
        if (StrUtil.isNotBlank(origin.getAuthorities())) {
            target.setAuthorities(AuthorityUtils.commaSeparatedStringToAuthorityList(origin.getAuthorities()));
        }

        // 设置资源 ID
        if (StrUtil.isNotBlank(origin.getResourceIds())) {
            target.setResourceIds(StringUtils.commaDelimitedListToSet(origin.getResourceIds()));
        }

        // 设置回调 URL
        if (StrUtil.isNotBlank(origin.getWebServerRedirectUri())) {
            target.setRegisteredRedirectUri(StringUtils.commaDelimitedListToSet(origin.getWebServerRedirectUri()));
        }

        // 设置权限范围
        if (StrUtil.isNotBlank(origin.getScope())) {
            target.setScope(StringUtils.commaDelimitedListToSet(origin.getScope()));
        }

        // 设置自动批准的权限
        if (StrUtil.isNotBlank(origin.getAutoapprove())) {
            target.setAutoApproveScopes(StringUtils.commaDelimitedListToSet(origin.getAutoapprove()));
        }

        // 设置 token 有效期
        if (origin.getAccessTokenValidity() != null) {
            target.setAccessTokenValiditySeconds(origin.getAccessTokenValidity());
        }

        // 设置刷新 token 有效期
        if (origin.getRefreshTokenValidity() != null) {
            target.setRefreshTokenValiditySeconds(origin.getRefreshTokenValidity());
        }

        // 解析附加信息
        String json = origin.getAdditionalInformation();
        if (StrUtil.isNotBlank(json)) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> additionalInformation = JSONUtil.toBean(json, Map.class);
                target.setAdditionalInformation(additionalInformation);
            } catch (Exception e) {
                log.warn("Could not decode JSON for additional information: " + json, e);
            }
        }

        log.debug("clientDetailsWrapper --> target: {}", JSONUtil.toJsonStr(target));

        return target;
    }

}
