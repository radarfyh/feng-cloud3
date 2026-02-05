# Swagger2移植到OpenApi3的要点

## 一、网关改造

### 包引入

```
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>com.github.xiaoymin</groupId>
                <artifactId>knife4j-dependencies</artifactId>
                <version>4.5.0</version>
                <scope>import</scope>
                <type>pom</type>
            </dependency>
        </dependencies>
    </dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.github.xiaoymin</groupId>
            <artifactId>knife4j-gateway-spring-boot-starter</artifactId>
        </dependency>
    </dependencies>
```

### 排除服务

- 1. 修改feng-gateway-dev.yml配置排除网关、认证服务

```
knife4j:
  gateway:
    discover:
      excluded-services:
        - feng-gateway2
        - feng-auth
```

- 2. 新增配置类排除网关、认证服务

```
import com.github.xiaoymin.knife4j.spring.gateway.Knife4jGatewayProperties;
import com.github.xiaoymin.knife4j.spring.gateway.discover.spi.GatewayServiceExcludeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MyExcludeService implements GatewayServiceExcludeService {
    @Override
    public Set<String> exclude(Environment environment, Knife4jGatewayProperties properties, List<String> services) {
        if (!CollectionUtils.isEmpty(services)){
            return services.stream().filter(s -> s.contains("feng-auth") || s.contains("feng-gateway2")).collect(Collectors.toSet());
        }
        return new TreeSet<>();
    }
}
```

### 修改feng-gateway-dev.yml配置开启knife4j

```
knife4j:
  gateway:
    enabled: true
```

### 修改feng-gateway-dev.yml配置开启注册中心自动寻找服务

```
knife4j:
  gateway:
    strategy: discover
    discover:
      enabled: true
      version: openapi3
spring:
  cloud:
    discovery:
      reactive:
        enabled: true
    loadbalancer:
      retry:
        enabled: true
    gateway:
      discovery:
        locator:
          enabled: true # 启用了自动根据服务名建立路由
          lower-case-service-id: true
```

发现问题：动态路由配置sys_route_conf和openapi3通过discover发现冲突。
，所以，要转换为手动配置到yml，弃用动态路由配置sys_route_conf

```
spring:
  cloud:
    gateway:
      routes:
        - id: feng-user2-biz
          uri: lb://feng-user2-biz
          order: 1
          predicates:
            - Path=/admin/**
          filters:
            - name: RequestRateLimiter
              args:
                key-resolver: "#{@remoteAddrKeyResolver}"
                redis-rate-limiter.replenishRate: 1000
                redis-rate-limiter.burstCapacity: 1000
        - id: feng-auth
          uri: lb://feng-auth
          order: 2
          predicates:
            - Path=/auth/**
          filters:
            - ValidateCodeGatewayFilter  
            - PasswordDecoderFilter     
        - id: feng-msg-biz
          uri: lb://feng-msg-biz
          order: 99
          predicates:
            - Path=/msg/**
        - id: feng-log2-biz
          uri: lb://feng-log2-biz
          order: 100
          predicates:
            - Path=/log/**
        - id: feng-ai-biz
          uri: lb://feng-ai-biz
          order: 101
          predicates:
            - Path=/aigc/**
```

### 修改feng-gateway-dev.yml配置指定排序方式

```
knife4j:
  gateway:
    tags-sorter: order
    operations-sorter: order
```

### 修改application-dev.yml配置放过openapi3和knife4j链接

```
security:
  oauth2:
    client:
      ignore-urls:
        - /v2/api-docs/**
        - /v3/api-docs/**
        - /doc.html
        - /webjars/**
        - /swagger-ui/**
        - /swagger-ui.html
        - /swagger-resources/**
    resource:
      loadBalanced: true
      token-info-uri: http://192.168.40.111:2000/auth/oauth/check_token
```

### 问题1 SyntaxError: Unexpected token '<', "<!DOCTYPE "... is not valid JSON

```
app.c31badf5.js:1 SyntaxError: Unexpected token '<', "<!DOCTYPE "... is not valid JSON
    at JSON.parse (<anonymous>)
    at Object.json5parse (app.c31badf5.js:1:364220)
    at r.transformResponse (app.c31badf5.js:1:196382)
    at chunk-vendors.d51cf6f8.js:2:2003275
    at Object.u [as forEach] (chunk-vendors.d51cf6f8.js:2:2005150)
    at e.exports (chunk-vendors.d51cf6f8.js:2:2003248)
    at chunk-vendors.d51cf6f8.js:2:1328322
```

- 原因分析：spring-security放行路由（忽略链接）不完整
- 解决办法：增加spring-security的忽略链接

### 问题2 404

```
Access to XMLHttpRequest at 'http://192.168.40.2:2001/token/login' (redirected from 'http://192.168.0.191:10093/feng-auth/v3/api-docs') from origin 'http://192.168.0.191:10093' has been blocked by CORS policy: Response to preflight request doesn't pass access control check: No 'Access-Control-Allow-Origin' header is present on the requested resource.Understand this errorAI
app.c31badf5.js:1 Error: Network Error
    at chunk-vendors.d51cf6f8.js:2:679693
    at XMLHttpRequest.<anonymous> (chunk-vendors.d51cf6f8.js:2:1869143)
```

- 原因分析：所配置的服务转发出现问题，从而导致knife4j无法自动生成
- 解决办法：增加spring-security的忽略链接

### 参考文档

- Spring Cloud Gateway网关下Knife4j文档聚合,以及动态路由的读取和代码配置：https://blog.csdn.net/x910380566/article/details/139830652
- https://blog.csdn.net/weixin_74583509/article/details/146125422

## 二、普通服务改造

### 包引入

```
    <dependencies>
        <dependency>
            <groupId>work.metanet</groupId>
            <artifactId>feng-library2-swagger</artifactId>
        </dependency>
    </dependencies>
```

### 修改实体注解

```
@Data
@Schema(name = "消息日志",description = "消息日志表")
@EqualsAndHashCode(callSuper = true)
public class MsgLog extends Model<MsgLog> {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "雪花算法id")
    private String id;

    @Schema(description = "模板分类id")
    private Integer templateTypeId;
    ......
```

### 修改控制器注解

```
@RestController
@AllArgsConstructor
@RequestMapping("msgLog")
@Tag(name = "消息日志表模块")
public class MsgLogController {
    private final MsgLogService msgLogService;

    @GetMapping("/page")
    @Operation(summary = "分页查询所有数据")
    public R selectAll(Page page, MsgLogDTO msgLogDTO) {
        return R.ok(
            this.msgLogService.page(
                    page, Wrappers.<MsgLog>lambdaQuery()
                        .eq(StrUtil.isNotBlank(msgLogDTO.getRequestNo()), MsgLog::getRequestNo, msgLogDTO.getRequestNo())
                        .eq(null != msgLogDTO.getServiceTypeId(), MsgLog::getServiceTypeId, msgLogDTO.getServiceTypeId())
                        .eq(StrUtil.isNotBlank(msgLogDTO.getRecipientId()), MsgLog::getRecipientId, msgLogDTO.getRecipientId())
                        .eq(StrUtil.isNotBlank(msgLogDTO.getMsgGrade()), MsgLog::getMsgGrade, msgLogDTO.getMsgGrade())
                        .eq(StrUtil.isNotBlank(msgLogDTO.getHandleStatus()), MsgLog::getHandleStatus, msgLogDTO.getHandleStatus())
                        .eq(StrUtil.isNotBlank(msgLogDTO.getIsValid()), MsgLog::getIsValid, msgLogDTO.getIsValid())
                        .like(StrUtil.isNotBlank(msgLogDTO.getRecipientName()), MsgLog::getRecipientName, msgLogDTO.getRecipientName())
                        .ge(null != msgLogDTO.getStartTime(), MsgLog::getCreateTime, msgLogDTO.getStartTime())
                        .le(null != msgLogDTO.getEndTime(), MsgLog::getCreateTime, msgLogDTO.getEndTime())
                        .orderByDesc(MsgLog::getCreateTime)
                )
            );
    }
    ......
```

### swagger2和openapi3的注解对比

```
   |swagger2            |swagger3                             |desc
   |--------------------|-------------------------------------|--------------------------------
   |@Api                |@Tag(name = “xxx”)                   |标记一个类是 API 的入口点，描述整个控制器的功能。
   |@ApiOperation       |@Operation(summary = “xxx”)          |描述一个方法的功能（如 GET、POST 请求）。新版支持更丰富的元信息
   |@ApiImplicitParams  |@Parameters                          |描述非显式声明的参数（如查询参数、表单参数等）。
   |@ApiImplicitParam   |@Parameter                           |描述单个非显式声明的参数。
   |@ApiModel           |@Schema                              |描述实体类的模型信息。新版支持嵌套对象和复杂类型定义
   |@ApiModelProperty   |@Schema                              |描述实体类中字段的信息。新版支持更细粒度的约束（如 maxLength、minLength 等）。
   |@ApiResponse        |@ApiResponse                         |描述响应结果，新版支持更复杂的响应结构（如 content 属性定义响应体格式）。
   |@ApiParam           |@Parameter                           |描述方法参数，支持更多参数类型（如 in 属性指定参数位置）。
```

### 修改application-dev.yml

```
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    tags-sorter: alpha
    operations-sorter: alpha
    enabled: true
  group-configs:
    - group: 'default'
      paths-to-match: '/**'

knife4j:
  enable: true
  setting:
    language: zh-CN
    enable-swagger-models: true
    enable-document-manage: true
    enable-version-strategy: false
```

### 参考

- swagger全部注解，附swagger2和swagger3的注解区别 https://blog.csdn.net/qq_41694906/article/details/146340208
- Swagger升级指南：Swagger2与Swagger3注解差异揭秘 https://blog.csdn.net/jam_yin/article/details/135114395
