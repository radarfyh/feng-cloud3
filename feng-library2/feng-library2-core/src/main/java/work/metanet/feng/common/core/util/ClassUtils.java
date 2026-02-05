package work.metanet.feng.common.core.util;

import lombok.experimental.UtilityClass;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * 类工具类
 * <p>
 * 提供了用于获取方法参数信息、注解等常用工具方法。
 * </p>
 */
@UtilityClass
public class ClassUtils extends org.springframework.util.ClassUtils {

    private final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    /**
     * 获取构造函数参数信息
     * <p>
     * 该方法返回构造器指定参数索引位置的 {@link MethodParameter} 对象。
     * </p>
     *
     * @param constructor    构造器
     * @param parameterIndex 参数序号
     * @return {@link MethodParameter}
     */
    public MethodParameter getMethodParameter(Constructor<?> constructor, int parameterIndex) {
        MethodParameter methodParameter = new SynthesizingMethodParameter(constructor, parameterIndex);
        methodParameter.initParameterNameDiscovery(PARAMETER_NAME_DISCOVERER);
        return methodParameter;
    }

    /**
     * 获取方法参数信息
     * <p>
     * 该方法返回方法指定参数索引位置的 {@link MethodParameter} 对象。
     * </p>
     *
     * @param method         方法
     * @param parameterIndex 参数序号
     * @return {@link MethodParameter}
     */
    public MethodParameter getMethodParameter(Method method, int parameterIndex) {
        MethodParameter methodParameter = new SynthesizingMethodParameter(method, parameterIndex);
        methodParameter.initParameterNameDiscovery(PARAMETER_NAME_DISCOVERER);
        return methodParameter;
    }

    /**
     * 获取方法上的注解
     * <p>
     * 该方法通过给定的方法及注解类型，查找该方法上是否有指定的注解。
     * </p>
     *
     * @param method         方法
     * @param annotationType 注解类型
     * @param <A>            注解类型的泛型
     * @return {@link Annotation}
     */
    public <A extends Annotation> A getAnnotation(Method method, Class<A> annotationType) {
        // 获取最具体的方法
        Method specificMethod = getMostSpecificMethod(method, method.getDeclaringClass());
        // 获取桥接方法
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
        // 先找方法注解
        A annotation = AnnotatedElementUtils.findMergedAnnotation(specificMethod, annotationType);
        if (annotation != null) {
            return annotation;
        }
        // 如果方法上没有，继续找类上的注解
        return AnnotatedElementUtils.findMergedAnnotation(specificMethod.getDeclaringClass(), annotationType);
    }

    /**
     * 获取HandlerMethod上的注解
     * <p>
     * 该方法通过给定的 {@link HandlerMethod} 和注解类型，查找方法或类上的注解。
     * </p>
     *
     * @param handlerMethod  {@link HandlerMethod}
     * @param annotationType 注解类型
     * @param <A>            注解类型的泛型
     * @return {@link Annotation}
     */
    public <A extends Annotation> A getAnnotation(HandlerMethod handlerMethod, Class<A> annotationType) {
        // 查找方法上的注解
        A annotation = handlerMethod.getMethodAnnotation(annotationType);
        if (annotation != null) {
            return annotation;
        }
        // 如果方法上没有，继续找类上的注解
        return AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getBeanType(), annotationType);
    }
}
