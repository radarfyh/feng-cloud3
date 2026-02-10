package ltd.huntinginfo.feng.agent.api.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AppAuthErrorCode {
    INVALID_APPKEY("1301", "无效的应用标识"),
    SECRET_MISMATCH("1302", "应用密钥不匹配"),
    PARAM_MISSING("1303", "缺少必要的参数"),
    APPKEY_INVALID("1304", "无效的应用标识"),
    APP_DISABLED("1305", "应用已被禁用"),
    APP_EXPIRED("1306", "应用凭证已过期"),
    SYSTEM_ERROR("1307", "认证服务异常"),
    TIMESTAMP_EXPIRED("1308", "时间戳已过期"),
    REPLAY_ATTACK("1309","请求已被处理"),
    CALLER_INVALID("1310", "无效的调用者标识"),
    SIGNATURE_INVALID("1311", "签名验证失败"),
    APP_NOT_EXISTED("1312", "应用不存在"),
    PERMISSION_DENIED("1399", "权限不足");
	
	private String code;
	
	private String message;
}
