package work.metanet.feng.common.core.config;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate 配置类 (适配 Spring Boot 3/4 + HttpClient 5)
 */
@Configuration
public class RestTemplateConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(clientHttpRequestFactory());
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        // 1. 定义连接配置 (专门管理 TCP 握手超时) - HC5 新特性
        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(3000)) // <--- 连接超时在这里设置
                .build();

        // 2. 创建连接池管理器，并应用连接配置
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(200);
        connectionManager.setDefaultMaxPerRoute(20);
        connectionManager.setDefaultConnectionConfig(connectionConfig); // 应用配置

        // 3. 定义请求配置 (管理读取超时 和 从池获取连接超时)
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(3000)) // 从池获取连接超时
                .setResponseTimeout(Timeout.ofMilliseconds(5000))          // 读超时 (Socket Timeout)
                .build();

        // 4. 创建 HttpClient
        HttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();

        // 5. 创建 Spring 适配器
        // 由于 HttpClient 内部已经配置了所有超时，这里只需要直接传递即可
        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }
}