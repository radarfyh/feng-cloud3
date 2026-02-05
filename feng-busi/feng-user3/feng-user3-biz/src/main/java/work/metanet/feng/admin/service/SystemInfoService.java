package work.metanet.feng.admin.service;

/**
 * 系统信息服务接口
 *
 * @author fyh
 * @since 2024-09-17
 */
public interface SystemInfoService {
	int getCurrentUsers();
	int getCurrentCpuCount();
	double getCpuLoad();
	String getMacAddress();
	String getIpAddress();
}
