//package work.metanet.feng.admin.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.MediaType;
//import org.springframework.http.converter.HttpMessageConverter;
//import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//
//import java.nio.charset.StandardCharsets;
//import java.util.Collections;
//import java.util.List;
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//    
//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
//        
//        // 支持带charset的媒体类型
//        MediaType mediaType = new MediaType(
//            MediaType.APPLICATION_JSON,
//            StandardCharsets.UTF_8
//        );
//        converter.setSupportedMediaTypes(Collections.singletonList(mediaType));
//        
//        // 设置日期格式等自定义配置
//        converter.setObjectMapper(new ObjectMapper()
//            .registerModule(new JavaTimeModule())
//            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
//        );
//        
//        converters.add(0, converter);
//    }
//}