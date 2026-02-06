package work.metanet.feng.common.security.exception;

import lombok.Getter;

/**
 * 自定义认证授权异常，用于在安全认证过程中抛出业务异常
 * 不再继承已废弃的 OAuth2Exception
 */
@Getter
public class FengAuth2Exception extends RuntimeException {

    /**
     * 错误码，用于提供更详细的错误信息
     */
    private final String errorCode;

    /**
     * 构造一个新的 FengAuth2Exception 实例，接受错误消息
     *
     * @param msg 错误消息
     */
    public FengAuth2Exception(String msg) {
        super(msg);
        this.errorCode = null;
    }

    /**
     * 构造一个新的 FengAuth2Exception 实例，接受错误消息和异常
     *
     * @param msg 错误消息
     * @param t   异常
     */
    public FengAuth2Exception(String msg, Throwable t) {
        super(msg, t);
        this.errorCode = null;
    }

    /**
     * 构造一个新的 FengAuth2Exception 实例，接受错误消息和错误码
     *
     * @param msg       错误消息
     * @param errorCode 错误码
     */
    public FengAuth2Exception(String msg, String errorCode) {
        super(msg);
        this.errorCode = errorCode;
    }
}