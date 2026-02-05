package work.metanet.feng.common.data.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;

/**
 * Jackson2Config 配置类
 * <p>
 * 该类用于配置 Jackson 的 ObjectMapper，解决日期时间格式化问题。通过注册 JavaTimeModule 使得
 * Java 8 时间类型（如 LocalDateTime）能够被正确序列化和反序列化。
 * </p>
 * <p>
 * 另外，`findAndRegisterModules()` 方法用于自动注册 Jackson 可用的其他模块，如处理其他类型的序列化和反序列化。
 * </p>
 *
 * @author edison
 * @date 2025-2-19
 */
@Configuration
public class Jackson2Config implements WebMvcConfigurer {

    /**
     * 配置 ObjectMapper，解决 Java 8 时间类的序列化问题
     * <p>
     * 默认的 Jackson 配置无法正确处理 Java 8 的时间类（例如 LocalDateTime），
     * 所以这里使用了 `JavaTimeModule` 来注册相关的时间序列化与反序列化规则。
     * </p>
     *
     * @return ObjectMapper 配置完成的对象映射器
     */
    @Bean
    public ObjectMapper objectMapper() {
        // 创建 ObjectMapper 实例
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 创建 JavaTimeModule 实例，处理 Java 8 的时间类
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        
        // 注册时间模块
        objectMapper.registerModule(javaTimeModule);
        
        // 自动注册其他模块（如 JSR-310 的时间模块等）
        objectMapper.findAndRegisterModules();
        
        // 返回配置后的 ObjectMapper
        return objectMapper;
    }
}
