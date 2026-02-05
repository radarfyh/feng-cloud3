package work.metanet.feng.common.core.config;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import work.metanet.feng.common.core.constant.CommonConstants;

@Data
public class TenantOfHeader {
	private String tenantId;
	private String tenantCode;
	private String url;
	private List<Integer> roles = new ArrayList<>();
	private Boolean isAdmin = false;
	private Integer source; // 1-前端，2-FengFeignTenantInterceptor,3-TenantRequestInterceptor
	private long timestamp = System.currentTimeMillis();
}
