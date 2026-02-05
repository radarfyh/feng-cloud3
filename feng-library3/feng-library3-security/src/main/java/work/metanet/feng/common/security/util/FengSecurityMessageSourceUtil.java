package work.metanet.feng.common.security.util;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;

/**
 * 自定义消息源工具类，用于处理Spring Security相关的异常信息和消息资源。
 * <p>
 * 该类扩展了 {@link ReloadableResourceBundleMessageSource}，并自定义了消息文件的加载和默认区域设置。
 * 用于替代 {@link org.springframework.security.core.SpringSecurityMessageSource}，以便自定义和国际化处理Spring Security的异常消息。
 * 建议所有异常都使用此工具类型 避免无法复写 SpringSecurityMessageSource
 * </p>
 * <p>
 * 可以通过 {@link #getAccessor()} 方法获取消息源访问器，以便获取国际化消息。
 * </p>
 */
public class FengSecurityMessageSourceUtil extends ReloadableResourceBundleMessageSource {

    /**
     * 构造函数，设置默认的消息源位置和默认语言环境。
     * <p>
     * 默认加载路径为 {@code classpath:messages/messages}，默认语言环境为简体中文。
     * </p>
     */
    public FengSecurityMessageSourceUtil() {
        // 设置消息源文件路径
        setBasename("classpath:messages/messages");
        // 设置默认语言为中文
        setDefaultLocale(Locale.CHINA);
    }

    /**
     * 获取 {@link MessageSourceAccessor}，用于访问消息源中的内容。
     * <p>
     * 通过此方法，可以获取到消息源的访问器，便于获取国际化的消息资源。
     * </p>
     * 
     * @return 返回一个 {@link MessageSourceAccessor} 实例
     */
    public static MessageSourceAccessor getAccessor() {
        // 使用单例实例化方法，避免每次创建新实例
        return new MessageSourceAccessor(new FengSecurityMessageSourceUtil());
    }
}