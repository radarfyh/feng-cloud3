package work.metanet.feng.common.security.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import work.metanet.feng.common.core.constant.enums.BusinessEnum;
import work.metanet.feng.common.security.component.FengAuth2ExceptionSerializer;
import org.springframework.http.HttpStatus;

/**
 * FengMethodNotAllowedException 是一个自定义的 OAuth2 异常类，表示 HTTP 方法不允许的异常。
 * <p>
 * 该异常用于处理 HTTP 请求方法不符合服务器支持的情况下抛出的错误。
 * 比如当客户端发送一个不允许的方法（如 PUT 或 DELETE）时，会抛出此异常。
 * </p>
 * <p>
 * 异常返回的 HTTP 状态码为 405，错误代码为 "method_not_allowed"。
 * </p>
 */
@JsonSerialize(using = FengAuth2ExceptionSerializer.class)
public class FengMethodNotAllowedException extends FengAuth2Exception {

    /**
     * 构造函数，初始化异常信息和原因。
     * 
     * @param msg 异常消息
     * @param t 异常的根本原因（可选）
     */
    public FengMethodNotAllowedException(String msg, Throwable t) {
        super(msg, t);  // 调用父类构造函数
    }
}
