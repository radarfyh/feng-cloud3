package work.metanet.feng.common.core.exception;

/**
 * 自定义运行时异常类
 * <p>
 * 该异常类用于封装自定义的运行时异常，支持不同的构造方法来传递异常消息、原因、堆栈跟踪等信息。
 * </p>
 */
public class CustomRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 带消息的构造方法
     * <p>
     * 该构造方法用于传递异常消息。
     * </p>
     * 
     * @param message 异常消息
     */
    public CustomRuntimeException(String message) {
        super(message);
    }

    /**
     * 带原因的构造方法
     * <p>
     * 该构造方法用于传递异常原因。
     * </p>
     * 
     * @param cause 原因异常
     */
    public CustomRuntimeException(Throwable cause) {
        super(cause);
    }

    /**
     * 带消息和原因的构造方法
     * <p>
     * 该构造方法用于传递异常消息和异常原因。
     * </p>
     * 
     * @param message 异常消息
     * @param cause   原因异常
     */
    public CustomRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 带消息、原因、抑制和堆栈跟踪标志的构造方法
     * <p>
     * 该构造方法用于传递异常消息、异常原因，抑制是否可用标志以及是否可写堆栈跟踪标志。
     * </p>
     * 
     * @param message             异常消息
     * @param cause               原因异常
     * @param enableSuppression   是否启用抑制
     * @param writableStackTrace  是否可写堆栈跟踪
     */
    public CustomRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
