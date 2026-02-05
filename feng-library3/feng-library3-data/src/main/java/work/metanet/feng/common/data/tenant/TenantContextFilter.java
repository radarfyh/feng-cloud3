package work.metanet.feng.common.data.tenant;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import work.metanet.feng.common.core.config.TenantOfHeader;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.core.constant.enums.BuiltInRoleEnum;
import work.metanet.feng.common.core.util.JasyptUtil;
import work.metanet.feng.common.security.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * 租户上下文过滤器<br>
 * 该过滤器用于从请求头或请求参数中获取租户信息，并设置到租户上下文中。
 * <p>
 * 注意：该过滤器会在请求处理完后清除租户上下文，确保租户上下文不会泄露到其他请求。
 * </p>
 *
 * @author EdisonFeng
 * @date 2025/06/15
 */
@Slf4j
@Component
public class TenantContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        try {
        	TenantContextHolder.setTenant(SecurityUtils.resolveTenantIdSimple(request));
        	
            filterChain.doFilter(request, response);
        
        } catch(Exception e) {
        	throw new IOException(e.getMessage());
        } finally {
            TenantContextHolder.clear(); // 确保清除上下文
        }
    }
    

}
