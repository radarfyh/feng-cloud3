package work.metanet.feng.common.core.config;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration; 

import java.util.concurrent.TimeUnit;

/**
 * RestTemplate 配置类 (适配 Spring Boot 3/4 + HttpClient 5)
 */
@Configuration
public class RestTemplateConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        // 创建 RestTemplate 并设置请求工厂
        return new RestTemplate(clientHttpRequestFactory());
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        // 1. 创建连接池管理器
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(200); // 最大连接数
        connectionManager.setDefaultMaxPerRoute(20); // 每个路由的最大连接数

        // 2. 配置请求参数 (超时时间)
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(3000)) // 从连接池获取连接的超时时间
                .setResponseTimeout(Timeout.ofMilliseconds(5000))          // 读取数据的超时时间
                .build();

        // 3. 创建 HttpClient 实例
        HttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                // 保持连接存活时间策略，可选
                // .setKeepAliveStrategy(...) 
                .build();

        // 4. 创建 Spring 的适配器工厂
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        
        // 设置连接超时 (握手时间)
        factory.setConnectionRequestTimeout(Duration.ofMillis(3000)); 

        return factory;
    }
}