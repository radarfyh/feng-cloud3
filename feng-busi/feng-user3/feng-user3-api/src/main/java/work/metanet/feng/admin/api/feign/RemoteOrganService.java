package work.metanet.feng.admin.api.feign;

import work.metanet.feng.admin.api.entity.SysOrgan;
import work.metanet.feng.common.core.constant.SecurityConstants;
import work.metanet.feng.common.core.constant.ServiceNameConstants;
import work.metanet.feng.common.core.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/*
 *
 * @Description:
 * @author edison
 * @date 2022/5/6
 * @param: null
 * @return
 */
@FeignClient(contextId = "remoteOrganService", value = ServiceNameConstants.USER2_SERVICE)
public interface RemoteOrganService {

    /**
     * 查询组织列表
     *
     * @param from 是否内部调用
     * @return
     */
    @GetMapping("/sysOrgan/list")
    R<List<SysOrgan>> list(@RequestHeader(SecurityConstants.FROM) String from);

}
