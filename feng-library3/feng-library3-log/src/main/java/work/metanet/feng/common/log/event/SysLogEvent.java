package work.metanet.feng.common.log.event;

import work.metanet.feng.admin.api.dto.SysLogDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统日志事件
 */
@Getter
@AllArgsConstructor
public class SysLogEvent {
	private final SysLogDTO sysLog;
}
