package ltd.huntinginfo.feng.ai.config;

import lombok.Data;

import java.util.Objects;

import jakarta.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 代理配置属性类
 * 有些国外模型必须开启VPN代理才能访问，例如openai的向量模型服务
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Data
@Component
@ConfigurationProperties(prefix = "feng.proxy")
public class ProxyProperties {
    private boolean enabled;
    private HttpProxy http;
    private HttpsProxy https;

    @Data
    public static class HttpProxy {
        private String host;
        private int port;
    }

    @Data
    public static class HttpsProxy {
        private String host;
        private int port;
    }
    
    /**
     * 在 Bean 初始化后自动设置系统代理
     * 不自动加载，需要时加载，因为nacos位于本地无法识别
     */
//    @PostConstruct
//    public void init() {
//        if (enabled) {
//            setSystemProxy();
//        }
//    }

    /**
     * 配置系统代理
     */
    private void setSystemProxy() {
        if (Objects.nonNull(http)) {
            System.setProperty("http.proxyHost", http.getHost());
            System.setProperty("http.proxyPort", String.valueOf(http.getPort()));
        }
        if (Objects.nonNull(https)) {
            System.setProperty("https.proxyHost", https.getHost());
            System.setProperty("https.proxyPort", String.valueOf(https.getPort()));
        }
        System.out.println("系统代理已启用: " + this);
    }
}