package work.metanet.feng.common.data.tenant;

import work.metanet.feng.common.core.config.TenantOfHeader;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.core.util.JasyptUtil;
import work.metanet.feng.common.security.util.SecurityUtils;

import java.util.Collection;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Feign 请求拦截器，用于注入租户信息
 * <p>
 * 该拦截器用于将租户信息（机构编码）注入到每个Feign请求的Header中，以确保多租户环境下的请求携带必要的租户标识。
 * </p>
 */
@Slf4j
@AllArgsConstructor
public class FengFeignTenantInterceptor implements RequestInterceptor {
	
    /**
     * 拦截Feign请求，注入租户的机构编码
     * <p>
     * 在每次发送Feign请求前，此方法会检查当前租户的机构编码（从TenantContextHolder中获取），
     * 如果机构编码存在，则将其加入请求的Header中；如果为空，则跳过该操作。
     * </p>
     * @param requestTemplate Feign请求模板
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
//        TenantOfHeader tenant = TenantContextHolder.getTenant();
    	String tenant = TenantContextHolder.getTenant();
        Collection<String> tenantHeaders = requestTemplate.headers().get(CommonConstants.TENANT_HEADER_KEY);
        String strTenantFromHeader = (tenantHeaders != null && !tenantHeaders.isEmpty())
                ? tenantHeaders.iterator().next()
                : null;
        
        String url = requestTemplate.url();
        log.debug("intercept-->拦截Feign请求，内容：{}，URL: {}", strTenantFromHeader, url);

        if (StrUtil.isBlank(tenant)) {
            // 如果租户ID为空，跳过该请求的注入
            log.warn("TenantContext 中的租户ID为空. URL={}", url);
        }
        
        // 将租户ID加入请求的Header
        try {
			log.debug("intercept-->修改feign请求头，租户{}，URL: {}", tenant, url);
			requestTemplate.header(CommonConstants.TENANT_HEADER_KEY, tenant);
            
		} catch (Exception e) {
			log.error("apply--》异常报错：{}，租户信息：{},URL:{}", e.getMessage(), tenant, url);
		}
    }
    

}


