package work.metanet.feng.common.security.component;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import work.metanet.feng.common.core.constant.SecurityConstants;
import work.metanet.feng.common.security.annotation.Inner;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 资源暴露处理器
 * <p>
 * 本类用于处理与权限相关的 URL 配置，主要功能包括过滤暴露接口，支持路径变量的正则匹配，允许对某些接口进行权限配置。
 * </p>
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnExpression("!'${security.oauth2.client.ignore-urls}'.isEmpty()")
@ConfigurationProperties(prefix = "security.oauth2.client")
public class PermitAllUrlResolver implements InitializingBean {

    private static final PathMatcher PATHMATCHER = new AntPathMatcher();
    private static final Pattern PATTERN = Pattern.compile("\\{(.*?)\\}");

    //private final WebApplicationContext applicationContext;
    
    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;
    
    @Getter
    @Setter
    private List<String> ignoreUrls = new ArrayList<>();

    /**
     * 初始化方法，扫描所有带有 @Inner 注解的接口，过滤需要暴露的接口 URL。
     * 
     * @throws Exception 如果扫描过程中出现错误
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        //RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();

        for (RequestMappingInfo info : map.keySet()) {
            HandlerMethod handlerMethod = map.get(info);

            // 1. 首先获取类上的 @Inner 注解
            Inner controller = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), Inner.class);

            // 2. 当类上不包含 @Inner 注解则获取该方法的注解
            if (controller == null) {
                Inner method = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), Inner.class);
                Optional.ofNullable(method).ifPresent(inner -> info.getPatternValues()
                        .forEach(url -> this.filterPath(url, info, map)));
                continue;
            }

            // 3. 当类上包含 @Inner 注解 判断handlerMethod 是否包含在 inner 类中
            Class<?> beanType = handlerMethod.getBeanType();
            Method[] methods = beanType.getDeclaredMethods();
            Method method = handlerMethod.getMethod();
            if (ArrayUtil.contains(methods, method)) {
                info.getPatternValues().forEach(url -> filterPath(url, info, map));
            }
        }
    }

    /**
     * 过滤暴露路径并进行权限配置
     * <p>
     * 此方法会过滤出符合条件的 URL，并根据配置将其暴露为不需要权限验证的接口。
     * </p>
     *
     * @param url  请求路径
     * @param info 请求映射信息
     * @param map  路由映射信息
     */
    private void filterPath(String url, RequestMappingInfo info, Map<RequestMappingInfo, HandlerMethod> map) {
        // 安全检查
        if (SecurityConstants.INNER_CHECK) {
            security(url, info, map);
        }

        List<String> methodList = info.getMethodsCondition().getMethods().stream().map(RequestMethod::name)
                .collect(Collectors.toList());
        String resultUrl = ReUtil.replaceAll(url, PATTERN, "*");

        if (CollUtil.isEmpty(methodList)) {
            ignoreUrls.add(resultUrl);  // 如果没有指定方法，则只暴露路径
        } else {
            ignoreUrls.add(String.format("%s|%s", resultUrl, CollUtil.join(methodList, StrUtil.COMMA)));
        }
    }

    /**
     * 针对路径变量进行安全检查，防止路径变量暴露
     * 
     * @param url 接口路径
     * @param rq 当前请求的元信息
     * @param map SpringMVC 接口列表
     */
    private void security(String url, RequestMappingInfo rq, Map<RequestMappingInfo, HandlerMethod> map) {
        // 判断 URL 是否是 restful path 形式
        if (!StrUtil.containsAny(url, StrUtil.DELIM_START, StrUtil.DELIM_END)) {
            return;
        }

        for (RequestMappingInfo info : map.keySet()) {
            Set<RequestMethod> methods = info.getMethodsCondition().getMethods();
            // 如果请求方法不匹配跳过
            if (!CollUtil.containsAny(methods, rq.getMethodsCondition().getMethods())) {
                continue;
            }

            Set<String> patterns = info.getPatternValues();
            for (String pattern : patterns) {
                // 跳过自身
                if (StrUtil.equals(url, pattern)) {
                    continue;
                }

                if (PATHMATCHER.match(url, pattern)) {
                    HandlerMethod rqMethod = map.get(rq);
                    HandlerMethod infoMethod = map.get(info);
                    log.warn("@Inner 标记接口 ==> {}.{} 使用不当，会额外暴露接口 ==> {}.{} 请知悉", rqMethod.getBeanType().getName(),
                            rqMethod.getMethod().getName(), infoMethod.getBeanType().getName(),
                            infoMethod.getMethod().getName());
                }
            }
        }
    }

    /**
     * 获取对外暴露的 URL，并注册到 Spring Security
     * 
     * @param registry Spring Security 的配置对象，用于注册 URL 权限
     */
    public void registry(AuthorizeHttpRequestsConfigurer<?>.AuthorizationManagerRequestMatcherRegistry registry) {
        for (String url : getIgnoreUrls()) {
            List<String> strings = StrUtil.split(url, "|");

            // 仅配置对外暴露的 URL，注册到 Spring Security 的为全部方法
            if (strings.size() == 1) {
                registry.requestMatchers(strings.get(0)).permitAll();
                continue;
            }

            // 当配置对外的 URL|GET,POST 这种形式，则获取方法列表并注册到 Spring Security
            if (strings.size() == 2) {
                for (String method : StrUtil.split(strings.get(1), StrUtil.COMMA)) {
                    registry.requestMatchers(HttpMethod.valueOf(method), strings.get(0)).permitAll();
                }
                continue;
            }

            log.warn("{} 配置无效，无法配置对外暴露", url);
        }
    }
}
