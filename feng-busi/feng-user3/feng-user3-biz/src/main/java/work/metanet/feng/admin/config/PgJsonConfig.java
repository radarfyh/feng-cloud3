//package work.metanet.feng.admin.config;
//
//import work.metanet.feng.admin.api.handler.PgJsonTypeHandler;
//
//import org.apache.ibatis.type.JdbcType;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
//import com.fasterxml.jackson.databind.JsonNode;
//
//@Configuration
//public class PgJsonConfig {
//
//    @Bean
//    public ConfigurationCustomizer configurationCustomizer() {
//        return configuration -> {
//            // 正确注册JSONObject类型与处理器的映射
//            configuration.getTypeHandlerRegistry().register(
//            		JsonNode.class, // 实体类字段类型
//                JdbcType.OTHER,   // PostgreSQL的JSON类型对应OTHER
//                new PgJsonTypeHandler()
//            );
//            
//            // 可选：全局设置空值处理
//            configuration.setJdbcTypeForNull(JdbcType.NULL);
//        };
//    }
//}