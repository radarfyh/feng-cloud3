package work.metanet.feng.common.core.exception;

/**
 * 验证码异常类
 * <p>
 * 该异常类用于处理与验证码相关的错误，继承自 {@link RuntimeException}。
 * </p>
 */
public class ValidateCodeException extends RuntimeException {

    private static final long serialVersionUID = -7285211528095468156L;

    /**
     * 无参构造方法
     * <p>
     * 该构造方法用于创建一个没有错误消息的验证码异常。
     * </p>
     */
    public ValidateCodeException() {
        super("验证码验证失败");  // 提供默认错误消息
    }

    /**
     * 带消息的构造方法
     * <p>
     * 该构造方法用于传递验证码验证错误的具体消息。
     * </p>
     * 
     * @param msg 异常消息
     */
    public ValidateCodeException(String msg) {
        super(msg);
    }
}
