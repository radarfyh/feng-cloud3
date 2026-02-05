package work.metanet.feng.common.security.component;

import cn.hutool.core.util.StrUtil;
import work.metanet.feng.common.core.constant.SecurityConstants;
import work.metanet.feng.common.security.annotation.Inner;
import work.metanet.feng.common.security.util.FengSecurityMessageSourceUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.AccessDeniedException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * FengSecurityInnerAspect 是一个 AOP 切面，用于处理服务间接口不进行鉴权的逻辑。
 * <p>
 * 该切面通过 {@link Inner} 注解标记的方法，判断是否需要绕过鉴权机制。如果接口请求的头部信息不符合指定的条件，
 * 则抛出权限不足异常（AccessDeniedException）。
 * </p>
 */
@Slf4j
@Aspect
@AllArgsConstructor
public class FengSecurityInnerAspect {

    private final HttpServletRequest request;

    /**
     * 环绕通知，处理所有被 {@link Inner} 注解标记的方法或类。
     * <p>
     * 该方法通过获取请求头信息，并判断是否有权限访问指定的接口。如果接口要求不进行鉴权，并且请求头信息不符合条件，
     * 则抛出 {@link AccessDeniedException} 异常。
     * </p>
     * 
     * @param point 目标方法的连接点
     * @param inner 内部注解对象，表示方法是否绕过鉴权
     * @return 方法的返回结果
     * @throws Throwable 异常抛出
     */
    @SneakyThrows
    @Around("@within(inner) || @annotation(inner)")
    public Object around(ProceedingJoinPoint point, Inner inner) {
        // 如果方法上没有 @Inner 注解，则尝试从类上获取该注解
        if (inner == null) {
            Class<?> aClass = point.getTarget().getClass();
            inner = AnnotationUtils.findAnnotation(aClass, Inner.class);
        }

        // 如果接口标记为需要内部调用权限验证，并且请求头不匹配，则抛出权限不足异常
        if (inner != null && inner.value()) {
            // 获取请求头中的 'FROM' 字段，用于验证是否来自合法来源            
            String headerFrom = request.getHeader(SecurityConstants.FROM);
            Boolean notMatch = !StrUtil.equals(SecurityConstants.FROM_IN, headerFrom);
            if (notMatch) {
	            log.warn("访问接口 {} 没有权限", point.toShortString());
	            // 使用国际化消息来提供更详细的异常信息
	            throw new AccessDeniedException(FengSecurityMessageSourceUtil.getAccessor().getMessage(
	                    "AbstractAccessDecisionManager.accessDenied", new Object[] { point.toShortString() }, "access denied"));
            }
        } 
        // 如果没有抛出异常，则正常执行目标方法
        return point.proceed();
    }
}
