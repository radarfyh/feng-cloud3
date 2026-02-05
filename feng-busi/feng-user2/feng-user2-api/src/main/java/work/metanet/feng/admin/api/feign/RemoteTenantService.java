package work.metanet.feng.admin.api.feign;

import work.metanet.feng.admin.api.entity.SysTenant;
import work.metanet.feng.admin.api.vo.SysTenantVO;
import work.metanet.feng.common.core.constant.SecurityConstants;
import work.metanet.feng.common.core.constant.ServiceNameConstants;
import work.metanet.feng.common.core.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(contextId = "remoteTenantService", value = ServiceNameConstants.USER2_SERVICE)
public interface RemoteTenantService {

    /**
     * 查询组织列表
     *
     * @param from 是否内部调用
     * @return
     */
    @GetMapping("/sysTenant/list")
    R<List<SysTenantVO>> list(@RequestBody SysTenant sysTenant, @RequestHeader(SecurityConstants.FROM) String from);

}
