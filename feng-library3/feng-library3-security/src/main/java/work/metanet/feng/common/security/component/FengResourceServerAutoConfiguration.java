package work.metanet.feng.common.security.component;

import work.metanet.feng.common.security.handler.RestResponseErrorHandler;
import org.springframework.beans.BeansException;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Map;

/**
 * 自动配置类，配置 RestTemplate 实例并处理安全相关配置。
 * <p>
 * 本类用于自动配置一个带有负载均衡（@LoadBalanced）功能的 RestTemplate，并为其添加全局的请求拦截器以及错误处理器。
 * </p>
 */
@ComponentScan("work.metanet.feng.common.security")
public class FengResourceServerAutoConfiguration implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    /**
     * 配置一个带负载均衡的 RestTemplate 实例，并为其添加全局的请求拦截器和错误处理器。
     * <p>
     * 该 RestTemplate 会自动加载所有的 ClientHttpRequestInterceptor 实现，并设置为默认的错误处理器，
     * 使得每个请求的错误能够通过自定义的 RestResponseErrorHandler 进行处理。
     * </p>
     * 
     * @return 配置好的 RestTemplate 实例
     */
    @Bean
    @Primary
    @LoadBalanced
    public RestTemplate lbRestTemplate() {
        // 获取所有的 ClientHttpRequestInterceptor 实现并添加到 RestTemplate 的拦截器列表中
        Map<String, ClientHttpRequestInterceptor> beansOfType = applicationContext
                .getBeansOfType(ClientHttpRequestInterceptor.class);
        
        // 创建并配置 RestTemplate 实例
        RestTemplate restTemplate = new RestTemplate();
        
        // 设置请求拦截器
        restTemplate.setInterceptors(new ArrayList<>(beansOfType.values()));
        
        // 设置自定义错误处理器
        restTemplate.setErrorHandler(new RestResponseErrorHandler());
        
        return restTemplate;
    }

    /**
     * 通过 ApplicationContextAware 接口设置 Spring 应用上下文
     * 
     * @param applicationContext Spring 上下文对象
     * @throws BeansException 如果发生 Spring Bean 创建异常
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}

