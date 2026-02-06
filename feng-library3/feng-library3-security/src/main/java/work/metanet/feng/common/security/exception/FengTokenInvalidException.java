package work.metanet.feng.common.security.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import work.metanet.feng.common.core.constant.enums.BusinessEnum;
import work.metanet.feng.common.security.component.FengAuth2ExceptionSerializer;
import org.springframework.http.HttpStatus;

/**
 * FengTokenInvalidException 表示 OAuth2 令牌无效的异常。
 * <p>
 * 该异常类用于表示访问的令牌无效的情况，通常在令牌已过期、格式不正确或被篡改时抛出。
 * 错误码为 "invalid_token"，HTTP 状态码为 424（Failed Dependency）。
 * </p>
 */
@JsonSerialize(using = FengAuth2ExceptionSerializer.class)
public class FengTokenInvalidException extends FengAuth2Exception {

    /**
     * 构造函数，初始化异常消息和原因。
     * 
     * @param msg 异常消息
     * @param t 异常的根本原因（可选）
     */
    public FengTokenInvalidException(String msg, Throwable t) {
        super(msg, t);  // 调用父类构造函数，传递异常消息和根本原因
    }
}
