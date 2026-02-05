package work.metanet.feng.common.log.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 日志类型
 */
@Getter
@RequiredArgsConstructor
public enum LogTypeEnum {
	NORMAL("0", "用户详细日志（CRUD）"),
	SECURITY("1", "安全日志"),
	AUDIT("2", "审计日志"),
	IMPORTANT("3", "重要业务的日志"),
	
	CRM("4", "CRM业务日志"),
	ERP("5", "ERP业务日志"),
	OA("6", "OA业务日志"),
	AGENT("7", "智能体平台业务日志"),
	HARDWARE("8", "非云硬件系统日志"),
	OS("9", "非云操作系统日志"),
	
	IAAS("A", "云上基础设施系统日志"),
	PAAS("B", "云上中间件系统日志"),
	SAAS("C", "云上业务系统日志"),

	ERROR("D", "错误日志"),
	OMS("E", "运维管理系统日志"),
	STARTUP("F", "服务启动日志"),
	
	OTHER("Z", "自定义分类：可在SysLog注解中直接写中文");

	/**
	 * 类型
	 */
	private final String type;

	/**
	 * 描述
	 */
	private final String description;
}