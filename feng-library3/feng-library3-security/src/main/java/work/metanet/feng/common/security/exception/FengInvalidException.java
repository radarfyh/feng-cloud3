package work.metanet.feng.common.security.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import work.metanet.feng.common.core.constant.enums.BusinessEnum;
import work.metanet.feng.common.security.component.FengAuth2ExceptionSerializer;

/**
 * FengInvalidException 是自定义的异常类，用于表示 OAuth2 错误类型 "invalid_exception"。
 * 该异常通常在 OAuth2 授权流程中遇到无效的请求时抛出。
 * <p>
 * 该异常为特定的错误类型，使用 HTTP 状态码 426（Upgrade Required）表示客户端需要升级协议或资源。
 * </p>
 */
@JsonSerialize(using = FengAuth2ExceptionSerializer.class)
public class FengInvalidException extends FengAuth2Exception {

    /**
     * 构造方法，接受错误消息和异常
     *
     * @param msg 错误消息
     * @param t   异常对象
     */
    public FengInvalidException(String msg, Throwable t) {
        super(msg, t);
    }

    /**
     * 返回 OAuth2 错误码 "invalid_exception"，该错误码表示无效的请求异常
     *
     * @return OAuth2 错误码
     */
    @Override
    public String getOAuth2ErrorCode() {
        return "invalid_exception";
    }

    /**
     * 返回 HTTP 错误码 426（Upgrade Required），表示客户端需要升级协议或资源
     *
     * @return HTTP 错误码
     */
    @Override
    public int getHttpErrorCode() {
        return BusinessEnum.WEB_UPGRADE_REQUIRED.getCode();
    }
}
