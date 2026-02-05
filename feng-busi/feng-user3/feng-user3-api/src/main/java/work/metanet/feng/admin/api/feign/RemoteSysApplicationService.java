package work.metanet.feng.admin.api.feign;

import work.metanet.feng.admin.api.entity.SysApplication;
import work.metanet.feng.common.core.constant.SecurityConstants;
import work.metanet.feng.common.core.constant.ServiceNameConstants;
import work.metanet.feng.common.core.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteSysApplicationService", value = ServiceNameConstants.USER2_SERVICE)
public interface RemoteSysApplicationService {

    /**
     * 校验appid和appSecret
     *
     * @param appid
     * @param appSecret
     * @param from:
     * @return R
     */
    @GetMapping("/sysApplication/checkAppId")
    R<SysApplication> checkAppId(@RequestParam("appid") String appid, @RequestParam("appSecret") String appSecret, @RequestHeader(SecurityConstants.FROM) String from);

}
