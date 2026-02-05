package work.metanet.feng.common.core.config;

import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.client.HttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 * RestTemplate 配置
 * <p>
 * 该配置类为RestTemplate提供了连接池支持，并设置了更好的性能和错误处理。
 * </p>
 */
@Configuration
public class RestTemplateConfiguration {

    /**
     * 配置 RestTemplate，支持连接池及自定义配置
     * <p>
     * 该方法创建一个带有连接池管理的HttpClient，并将其绑定到RestTemplate上，以提高性能。
     * </p>
     * 
     * @return 配置好的 RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        // 创建连接池管理器
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(200); // 设置最大连接数
        connectionManager.setDefaultMaxPerRoute(20); // 每个路由的最大连接数

        // 创建 HttpClient，绑定连接池
        HttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();

        // 使用 HttpComponentsClientHttpRequestFactory 来将 HttpClient 绑定到 RestTemplate
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        // 创建 RestTemplate，并设置自定义的工厂
        return new RestTemplate(factory);
    }
}
