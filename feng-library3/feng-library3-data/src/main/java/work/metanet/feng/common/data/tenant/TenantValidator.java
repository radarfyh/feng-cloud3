package work.metanet.feng.common.data.tenant;

import java.util.regex.Pattern;

import cn.hutool.core.util.StrUtil;

public class TenantValidator {
	// 租户ID验证：只允许数字且范围100-99999999999
	private static final Pattern TENANT_ID_PATTERN = Pattern.compile("^[1-9][0-9]{2,9}$");

    public static boolean isValid(String tenantId) {
        if (StrUtil.isBlank(tenantId)) {
            return false;
        }
        
        return TENANT_ID_PATTERN.matcher(tenantId).matches();
    }
}
