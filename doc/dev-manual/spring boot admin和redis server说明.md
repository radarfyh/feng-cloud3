# feng-bootadmin-server

新服务，支持微服务监控和redis server (二合一）

## 服务监控

基于Spring Boot Admin 2.7.16实现。

链接：http://192.168.40.2:2006/

使用custom.js植入redis菜单到Spring Boot Admin管理页面中，可以启动redis和停止redis

启用配置：

```
management:
  metrics.export.prometheus.enabled: true
  endpoints:
    enabled-by-default: false
    web:
      exposure:
        include: '*'  
      jmx:
        exposure:
          exclude: "*"         
  endpoint:
    metrics:
      enabled: true
    prometheus:
      enabled: true
    restart:
      enabled: true
    health:
      enabled: true
      show-details: always
      show-components: always
    info:
      enabled: true
  health:
    elasticsearch:
      enabled: false
```

## redis server

为了更方便测试，使用Redis Embed Server0.7.2 构建内置服务器。仅用于轻量级部署，注意大并发需要官方版本。

启用配置：

```
redis:
  embedded:
    enabled: true
    maxHeap: 16MB
    port: 6379
    requirepass: true
    password: radar
```