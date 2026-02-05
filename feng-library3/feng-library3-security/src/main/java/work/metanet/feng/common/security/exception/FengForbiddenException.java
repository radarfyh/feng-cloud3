package work.metanet.feng.common.security.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import work.metanet.feng.common.security.component.FengAuth2ExceptionSerializer;
import org.springframework.http.HttpStatus;

/**
 * FengForbiddenException 是一个自定义的异常，用于表示 "Forbidden" 错误。
 * 它继承自 FengAuth2Exception，并为“Forbidden”错误提供了自定义的错误码和 HTTP 错误码。
 * <p>
 * 该异常通常用于当客户端没有权限访问某些资源时触发，返回 HTTP 403 错误。
 * </p>
 */
@JsonSerialize(using = FengAuth2ExceptionSerializer.class)
public class FengForbiddenException extends FengAuth2Exception {

    /**
     * 构造一个 FengForbiddenException 实例，接受错误消息
     *
     * @param msg 错误消息
     */
    public FengForbiddenException(String msg) {
        super(msg);
    }

    /**
     * 构造一个 FengForbiddenException 实例，接受错误消息和异常
     *
     * @param msg 错误消息
     * @param t   异常
     */
    public FengForbiddenException(String msg, Throwable t) {
        super(msg, t);
    }

    /**
     * 返回 OAuth2 错误码，该错误码用于标识资源访问被拒绝
     *
     * @return OAuth2 错误码 "access_denied"
     */
    @Override
    public String getOAuth2ErrorCode() {
        return "access_denied";
    }

    /**
     * 返回 HTTP 错误码，该错误码用于标识 "Forbidden" 错误
     *
     * @return HTTP 错误码 403 (Forbidden)
     */
    @Override
    public int getHttpErrorCode() {
        return HttpStatus.FORBIDDEN.value();
    }
}
