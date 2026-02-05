package work.metanet.feng.admin.api.feign;

import work.metanet.feng.admin.api.entity.SysOauthClientDetails;
import work.metanet.feng.common.core.constant.SecurityConstants;
import work.metanet.feng.common.core.constant.ServiceNameConstants;
import work.metanet.feng.common.core.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/*
 * @Description: 客户端信息远程接口
 * @author EdisonFeng
 */
@FeignClient(contextId = "remoteClientDetailsService", value = ServiceNameConstants.USER2_SERVICE)
public interface RemoteClientDetailsService {

    /**
     * 通过clientId 查询客户端信息
     *
     * @param clientId 用户名
     * @param from     调用标志
     * @return R
     */
    @GetMapping("/client/getClientDetailsById/{clientId}")
    R<SysOauthClientDetails> getClientDetailsById(@PathVariable("clientId") String clientId, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 查询全部客户端
     *
     * @param from 调用标识
     * @return R
     */
    @GetMapping("/client/list")
    R<List<SysOauthClientDetails>> listClientDetails(@RequestHeader(SecurityConstants.FROM) String from);
}
