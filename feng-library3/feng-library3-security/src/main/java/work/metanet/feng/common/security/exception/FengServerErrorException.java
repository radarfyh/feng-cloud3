package work.metanet.feng.common.security.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import work.metanet.feng.common.core.constant.enums.BusinessEnum;
import work.metanet.feng.common.security.component.FengAuth2ExceptionSerializer;
import org.springframework.http.HttpStatus;

/**
 * FengServerErrorException 是一个自定义的 OAuth2 异常类，表示服务器内部错误。
 * <p>
 * 该异常用于处理服务器内部出现的错误，通常是因为服务器端未能完成请求处理。
 * 错误码为 "server_error"，HTTP 状态码为 500。
 * </p>
 * <p>
 * 该类继承自 FengAuth2Exception，重写了获取 OAuth2 错误码和 HTTP 错误码的方法。
 * </p>
 */
@JsonSerialize(using = FengAuth2ExceptionSerializer.class)
public class FengServerErrorException extends FengAuth2Exception {

    /**
     * 构造函数，初始化异常消息和原因。
     * 
     * @param msg 异常消息
     * @param t 异常的根本原因（可选）
     */
    public FengServerErrorException(String msg, Throwable t) {
        super(msg, t);  // 调用父类构造函数，传递异常消息和根本原因
    }

    /**
     * 获取 OAuth2 错误代码。
     * 
     * @return 返回错误代码 "server_error" 表示服务器内部错误。
     */
    @Override
    public String getOAuth2ErrorCode() {
        return "server_error";  // 错误代码，表示服务器错误
    }

    /**
     * 获取 HTTP 错误码。
     * 
     * @return 返回 HTTP 状态码 500，表示服务器内部错误。
     */
    @Override
    public int getHttpErrorCode() {
        // 使用 BusinessEnum 获取对应的错误码
        return BusinessEnum.WEB_INTERNAL_SERVER_ERROR.getCode();  // 获取业务枚举中的错误码
    }
}
