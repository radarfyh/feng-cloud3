package work.metanet.feng.common.core.factory;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.lang.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * 读取自定义 YAML 文件并将其转换为 Spring PropertySource 的工厂类
 * <p>
 * 该类用于将自定义的 YAML 配置文件加载为 Spring 的 PropertySource，
 * 使得 YAML 文件中的配置可以与其他配置源一样被 Spring 管理和使用。
 * </p>
 */
public class YamlPropertySourceFactory implements PropertySourceFactory {

    /**
     * 创建并返回一个 PropertySource，使用 YAML 文件中的属性。
     * 
     * @param name     属性源的名称
     * @param resource 资源（YAML 文件）
     * @return 一个 PropertySource 对象，包含 YAML 文件的属性
     * @throws IOException 如果读取 YAML 文件时发生错误
     */
    @Override
    public PropertySource<?> createPropertySource(@Nullable String name, EncodedResource resource) throws IOException {
        // 从 YAML 文件加载属性
        Properties propertiesFromYaml = loadYamlIntoProperties(resource);
        
        // 使用文件名或传入的 name 作为属性源的名称
        String sourceName = (name != null) ? name : resource.getResource().getFilename();
        
        // 返回 PropertiesPropertySource 对象
        return new PropertiesPropertySource(sourceName, propertiesFromYaml);
    }

    /**
     * 将 YAML 文件中的内容加载为 Properties 对象。
     * 
     * @param resource 资源（YAML 文件）
     * @return 包含 YAML 文件中内容的 Properties 对象
     * @throws FileNotFoundException 如果文件未找到
     * @throws IOException 如果其他 I/O 错误发生
     */
    private Properties loadYamlIntoProperties(EncodedResource resource) throws IOException {
        try {
            YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
            factory.setResources(resource.getResource());
            factory.afterPropertiesSet();
            return factory.getObject();
        } catch (IllegalStateException e) {
            // 捕获并处理 IllegalStateException，检查是否是 FileNotFoundException
            Throwable cause = e.getCause();
            if (cause instanceof FileNotFoundException) {
                throw (FileNotFoundException) cause;
            }
            throw e; // 重新抛出其他异常
        }
    }
}
