package work.metanet.feng.common.security.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import work.metanet.feng.common.core.constant.enums.BusinessEnum;
import work.metanet.feng.common.security.component.FengAuth2ExceptionSerializer;
import org.springframework.http.HttpStatus;

/**
 * FengUnauthorizedException 表示 OAuth2 无权访问的异常。
 * <p>
 * 该异常类用于表示用户在未授权情况下访问受保护的资源时抛出，错误码为 "unauthorized"，
 * HTTP 状态码为 401（Unauthorized）。
 * </p>
 */
@JsonSerialize(using = FengAuth2ExceptionSerializer.class)
public class FengUnauthorizedException extends FengAuth2Exception {

    /**
     * 构造函数，初始化异常消息和原因。
     * 
     * @param msg 异常消息
     * @param t 异常的根本原因（可选）
     */
    public FengUnauthorizedException(String msg, Throwable t) {
        super(msg, t);  // 调用父类构造函数，传递异常消息和根本原因
    }
}
