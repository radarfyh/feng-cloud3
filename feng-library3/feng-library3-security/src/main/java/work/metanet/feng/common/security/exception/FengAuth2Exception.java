package work.metanet.feng.common.security.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import work.metanet.feng.common.security.component.FengAuth2ExceptionSerializer;
import lombok.Getter;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * FengAuth2Exception 是一个自定义的 OAuth2Exception，用于处理 OAuth2 异常信息。
 * 该异常类扩展了 OAuth2Exception，支持自定义错误码和消息。
 * <p>
 * 该异常用于在 OAuth2 认证流程中抛出自定义的异常信息，结合 JSON 序列化器， 
 * 使得异常信息可以方便地转换成适当的格式进行响应。
 * </p>
 */
@JsonSerialize(using = FengAuth2ExceptionSerializer.class)
public class FengAuth2Exception extends OAuth2Exception {

    /**
     * 错误码，用于提供更详细的错误信息
     */
    @Getter
    private final String errorCode;

    /**
     * 构造一个新的 FengAuth2Exception 实例，接受错误消息
     *
     * @param msg 错误消息
     */
    public FengAuth2Exception(String msg) {
        super(msg);
        this.errorCode = null;  // 默认错误码为 null
    }

    /**
     * 构造一个新的 FengAuth2Exception 实例，接受错误消息和异常
     *
     * @param msg 错误消息
     * @param t   异常
     */
    public FengAuth2Exception(String msg, Throwable t) {
        super(msg, t);
        this.errorCode = null;  // 默认错误码为 null
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
