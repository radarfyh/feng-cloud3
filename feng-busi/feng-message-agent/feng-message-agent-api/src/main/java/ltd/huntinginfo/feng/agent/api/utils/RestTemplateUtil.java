package ltd.huntinginfo.feng.agent.api.utils;

import org.springframework.core.ParameterizedTypeReference;

import ltd.huntinginfo.feng.agent.api.entity.CenterResponse;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * RestTemplate 工具类
 */
public class RestTemplateUtil {
    
    /**
     * 创建 ParameterizedTypeReference
     * @param rawType 原始类型
     * @param typeArguments 泛型参数类型
     * @return ParameterizedTypeReference
     */
    public static <T> ParameterizedTypeReference<T> createTypeRef(Class<?> rawType, Class<?>... typeArguments) {
        return new ParameterizedTypeReference<T>() {
            @Override
            public Type getType() {
                return new ParameterizedType() {
                    @Override
                    public Type[] getActualTypeArguments() {
                        return typeArguments;
                    }
                    
                    @Override
                    public Type getRawType() {
                        return rawType;
                    }
                    
                    @Override
                    public Type getOwnerType() {
                        return null;
                    }
                    
                    @Override
                    public String toString() {
                        StringBuilder sb = new StringBuilder();
                        sb.append(rawType.getTypeName());
                        if (typeArguments.length > 0) {
                            sb.append("<");
                            for (int i = 0; i < typeArguments.length; i++) {
                                if (i > 0) {
                                    sb.append(", ");
                                }
                                sb.append(typeArguments[i].getTypeName());
                            }
                            sb.append(">");
                        }
                        return sb.toString();
                    }
                };
            }
        };
    }
    
    /**
     * 创建 CenterResponse 类型引用
     * @param dataType 数据类型
     * @return ParameterizedTypeReference
     */
    public static <T> ParameterizedTypeReference<CenterResponse<T>> createCenterResponseTypeRef(Class<T> dataType) {
        return createTypeRef(CenterResponse.class, dataType);
    }
}