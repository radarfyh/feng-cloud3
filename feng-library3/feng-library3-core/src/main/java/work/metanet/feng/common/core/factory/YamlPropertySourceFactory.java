package work.metanet.feng.common.core.factory;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.jspecify.annotations.Nullable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Yml配置加载工厂
 * <p>
 * 解决 Spring 的 @PropertySource 注解默认不支持 yaml 文件的问题
 * 已适配 Spring Boot 3/4，消除过时 API 警告
 *
 * @author feng
 */
public class YamlPropertySourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(@Nullable String name, EncodedResource resource) throws IOException {
        // 1. 确定资源名称：如果注解没写 name，就用文件名
        String sourceName = name != null ? name : resource.getResource().getFilename();

        // 2. 检查资源是否存在
        if (sourceName != null && resource.getResource().exists()) {
            // 3. 使用 Spring Boot 原生加载器 (核心优化点)
            YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
            
            // load 方法返回的是 List，因为 YAML 规范支持单文件中包含多文档 (--- 分隔)
            List<PropertySource<?>> sources = loader.load(sourceName, resource.getResource());
            
            if (sources != null && !sources.isEmpty()) {
                // 通常 @PropertySource 只需要加载第一个文档块
                return sources.get(0);
            }
        }

        // 4. 容错处理：如果文件不存在或为空，返回一个空的 PropertySource，避免启动报错
        return new org.springframework.core.env.MapPropertySource(
                sourceName != null ? sourceName : "empty", 
                Collections.emptyMap()
        );
    }
}