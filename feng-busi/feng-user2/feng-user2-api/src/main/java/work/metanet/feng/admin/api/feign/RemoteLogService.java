package work.metanet.feng.admin.api.feign;

import work.metanet.feng.admin.api.dto.SysLogDTO;
import work.metanet.feng.admin.api.dto.SysLogPageDTO;
import work.metanet.feng.admin.api.entity.SysLog;
import work.metanet.feng.common.core.constant.SecurityConstants;
import work.metanet.feng.common.core.constant.ServiceNameConstants;
import work.metanet.feng.common.core.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * RPC：日志操作
 */
@FeignClient(contextId = "remoteLogService", value = ServiceNameConstants.USER2_SERVICE)
public interface RemoteLogService {

    /**
     * 保存日志
     * @param sysLog 日志实体
     * @param from 是否内部调用
     * @return 操作结果
     */
    @PostMapping("/sysLog")
    R<Boolean> saveLog(@RequestBody SysLogDTO sysLog, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 分页查询日志
     * @param page 分页信息
     * @param sysLog 查询条件
     * @param from 是否内部调用
     * @return 分页结果
     */
    @PostMapping("/sysLog/pageRemote")
    R<Page<SysLog>> selectAll(@RequestBody SysLogPageDTO query, @RequestHeader(SecurityConstants.FROM) String from);
}
