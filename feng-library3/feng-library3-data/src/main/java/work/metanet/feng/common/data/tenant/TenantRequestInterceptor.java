package work.metanet.feng.common.data.tenant;

import work.metanet.feng.common.core.config.TenantOfHeader;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.core.util.JasyptUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 传递 RestTemplate 请求的租户ID拦截器<br>
 * 该拦截器会将当前请求的租户ID（如果存在）添加到请求头中，
 * 以便后端能够根据租户信息进行相应的数据隔离。
 * <p>
 * 该拦截器会从 TenantContextHolder 中获取租户ID并设置为请求头中的租户ID（`tenant_id`）。
 * 如果租户ID为空，则不会设置tenant_id。
 * </p>
 *
 * @author EdisonFeng
 * @date 2025/06/15
 */
@Slf4j
public class TenantRequestInterceptor implements ClientHttpRequestInterceptor {
    /**
     * 拦截请求并设置租户信息到请求头中。
     * 如果 TenantContextHolder 中包含租户ID，则将该编码加入请求头。
     *
     * @param request  当前请求
     * @param body     请求体
     * @param execution 请求执行器
     * @return ClientHttpResponse 执行后的响应
     * @throws IOException 请求执行过程中的 IO 异常
     */
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        String tenant = TenantContextHolder.getTenant();
        String strTenantFromHeader = request.getHeaders().getFirst(CommonConstants.TENANT_HEADER_KEY);
        log.debug("intercept-->拦截RestTemplate请求，内容：{}，URL: {}", strTenantFromHeader, request.getURI());
        // 如果租户ID存在，则添加到请求头中
        if (tenant != null) {
            // 设置请求头中的租户信息

			log.debug("intercept-->重设RestTemplate请求头，租户ID：{}，URL: {}", tenant, request.getURI());
            request.getHeaders().set(CommonConstants.TENANT_HEADER_KEY, tenant);
        } else {
            log.warn("No tenant information found in TenantContextHolder");  // 记录租户ID为空的警告日志
        }

        // 执行请求并返回响应
        return execution.execute(request, body);
    }

}
