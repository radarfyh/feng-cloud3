package work.metanet.feng.common.core.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * 国际化配置类
 * <p>
 * 该类配置了Spring的MessageSource，用于从i18n资源文件加载国际化消息。
 * </p>
 */
@Configuration
public class MessageSourceConfiguration {

    private static final String BASENAME = "classpath:i18n/messages";  // i18n资源文件的位置

    /**
     * 配置MessageSource，加载国际化消息
     * <p>
     * 该方法配置了一个ReloadableResourceBundleMessageSource，用于加载指定路径下的国际化文件。
     * </p>
     * 
     * @return 配置好的MessageSource
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        
        // 设置国际化文件的基础位置
        messageSource.setBasename(BASENAME);
        
        // 设置文件编码，确保处理中文等字符时不会出现乱码
        messageSource.setDefaultEncoding("UTF-8");
        
        // 设置缓存周期，以确保国际化文件的更新可以及时生效
        messageSource.setCacheSeconds(3600);  // 每小时刷新一次缓存
        
        return messageSource;
    }
}
