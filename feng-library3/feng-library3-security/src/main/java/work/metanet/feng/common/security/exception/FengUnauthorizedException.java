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

    /**
     * 获取 OAuth2 错误代码。
     * 
     * @return 返回错误代码 "unauthorized" 表示未授权。
     */
    @Override
    public String getOAuth2ErrorCode() {
        return "unauthorized";  // 返回固定的 OAuth2 错误码
    }

    /**
     * 获取 HTTP 错误码。
     * 
     * @return 返回 HTTP 状态码 401，表示未授权（Unauthorized）。
     */
    @Override
    public int getHttpErrorCode() {
        // 使用 BusinessEnum 获取对应的错误码
        return BusinessEnum.WEB_UNAUTHORIZED.getCode();  // 获取业务枚举中的错误码
    }
}
