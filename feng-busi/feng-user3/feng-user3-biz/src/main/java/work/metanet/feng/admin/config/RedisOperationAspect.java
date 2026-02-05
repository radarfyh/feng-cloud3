package work.metanet.feng.admin.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class RedisOperationAspect {
    
    @Around("execution(* org.springframework.data.redis.core.RedisOperations.*(..))")
    public Object logRedisOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        //log.debug("开始Redis操作: {}", methodName);
        try {
            Object result = joinPoint.proceed();
            //log.debug("Redis操作成功: {}", methodName);
            return result;
        } catch (Exception e) {
            log.error("Redis操作失败: " + methodName, e);
            throw e;
        }
    }
}
