package work.metanet.feng.common.core.config;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import work.metanet.feng.common.core.jackson.FengJavaTimeModule;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.web.servlet.filter.OrderedCharacterEncodingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Jackson配置，定义时间转换规则，默认时区设置等
 * <p>
 * 主要用于配置时间序列化格式、字符编码设置等。
 * </p>
 */
@Configuration
@ConditionalOnClass(ObjectMapper.class)
@AutoConfigureBefore(JacksonAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class JacksonConfiguration implements WebMvcConfigurer {

    private static final String ASIA_SHANGHAI = "Asia/Shanghai";

    /**
     * Jackson的自定义配置，设置时区、日期格式等
     * <p>
     * 该方法用于配置Jackson的ObjectMapper，设置默认时区为"Asia/Shanghai"，日期格式以及Long类型序列化为字符串。
     * </p>
     * 
     * @return Jackson2ObjectMapperBuilderCustomizer
     */
    @Bean
    @ConditionalOnMissingBean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> {
            builder.locale(Locale.CHINA);
            builder.timeZone(TimeZone.getTimeZone(ASIA_SHANGHAI));
            builder.simpleDateFormat(DatePattern.NORM_DATETIME_PATTERN); // yyyy-MM-dd HH:mm:ss
            builder.serializerByType(Long.class, ToStringSerializer.instance);
            builder.modules(new FengJavaTimeModule()); // 使用自定义JavaTimeModule
        };
    }

    /**
     * 增加GET请求参数中时间类型转换 {@link FengJavaTimeModule}
     * <p>
     * 该方法注册时间格式化规则，将GET请求参数中的时间字符串自动转换为相应的LocalTime、LocalDate或LocalDateTime。
     * </p>
     * 
     * @param registry FormatterRegistry
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setTimeFormatter(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN)); // HH:mm:ss
        registrar.setDateFormatter(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)); // yyyy-MM-dd
        registrar.setDateTimeFormatter(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)); // yyyy-MM-dd HH:mm:ss
        registrar.registerFormatters(registry);
    }

    /**
     * 设置字符编码过滤器，避免表单提交时出现中文乱码问题
     * <p>
     * 该方法配置了字符编码过滤器，强制使用UTF-8编码，以避免中文乱码问题。
     * </p>
     * 
     * @return OrderedCharacterEncodingFilter
     */
    @Bean
    public OrderedCharacterEncodingFilter characterEncodingFilter() {
        OrderedCharacterEncodingFilter filter = new OrderedCharacterEncodingFilter();
        filter.setEncoding(StandardCharsets.UTF_8.name());
        filter.setForceEncoding(true);
        filter.setOrder(Ordered.HIGHEST_PRECEDENCE); // 设置过滤器的优先级
        return filter;
    }
}
