package work.metanet.feng.common.feign;

import work.metanet.feng.common.feign.endpoint.FeignClientEndpoint;
import feign.Feign;
import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FengFeignClientsRegistrar;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * @author edison
 * @date 2021/1/15
 * <p>
 * feign 自动化配置
 */
@Configuration
@ConditionalOnClass(Feign.class)
@Import(FengFeignClientsRegistrar.class)
@AutoConfigureAfter(EnableFeignClients.class)
public class FengFeignAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnAvailableEndpoint
	public FeignClientEndpoint feignClientEndpoint(ApplicationContext context) {
		return new FeignClientEndpoint(context);
	}
	
	/*
	 * @date 2025-2-19
	 * 解决如下问题： 
	 * java.time.format.DateTimeParseException: Text '2023-08-29T17:54:38' could not be parsed at index 10
	 */
    @Bean
    public Decoder feignDecoder() {
        // 创建 ObjectMapper，注册 JavaTimeModule 处理 LocalDateTime
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // 注册 Java 8 时间模块

        // 使用 Spring 的 MappingJackson2HttpMessageConverter
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
        
        // 返回自定义的 JacksonDecoder，用于 Feign 客户端
        return new JacksonDecoder(objectMapper);
    }

}
