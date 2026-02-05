package work.metanet.feng.admin.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import work.metanet.feng.admin.api.entity.SysTenant;
import work.metanet.feng.admin.api.entity.SysTenantConfig;

@Data
@EqualsAndHashCode(callSuper = false)
public class SysTenantVO extends SysTenant {
	@Schema(description = "租户配置对象")
	SysTenantConfig config;
}
