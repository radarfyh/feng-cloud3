package work.metanet.feng.common.core.util;

import lombok.experimental.UtilityClass;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * i18n 工具类
 * <p>
 * 该类提供从资源文件中获取本地化（国际化）信息的功能。
 * </p>
 */
@UtilityClass
public class MsgUtils {

    /**
     * 获取中文错误信息
     * <p>
     * 通过错误码（code）从消息源中获取对应的中文错误信息。
     * </p>
     *
     * @param code 错误码
     * @return 错误信息
     */
    public String getMessage(String code) {
        MessageSource messageSource = SpringContextHolder.getBean(MessageSource.class);
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    /**
     * 获取带参数的中文错误信息
     * <p>
     * 通过错误码（code）和参数从消息源中获取对应的中文错误信息。
     * </p>
     *
     * @param code 错误码
     * @param objects 错误信息中需要替换的参数
     * @return 错误信息
     */
    public String getMessage(String code, Object... objects) {
        MessageSource messageSource = SpringContextHolder.getBean(MessageSource.class);
        return messageSource.getMessage(code, objects, LocaleContextHolder.getLocale());
    }
}
