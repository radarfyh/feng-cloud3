package work.metanet.feng.common.core.sensitive;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import work.metanet.feng.common.core.constant.enums.SensitiveTypeEnum;
import work.metanet.feng.common.core.util.DesensitizedUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.Objects;

/**
 * 脱敏序列化器
 * <p>
 * 该序列化器用于对敏感数据字段进行脱敏处理，支持不同类型的敏感数据，如身份证、手机号码等。
 * </p>
 */
@NoArgsConstructor
@AllArgsConstructor
public class SensitiveSerialize extends JsonSerializer<String> implements ContextualSerializer {

    private SensitiveTypeEnum type;
    private Integer prefixNoMaskLen;
    private Integer suffixNoMaskLen;
    private String maskStr;

    /**
     * 序列化方法
     * <p>
     * 根据不同的敏感数据类型，调用对应的脱敏工具类进行数据脱敏。
     * </p>
     *
     * @param origin              原始数据
     * @param jsonGenerator       JSON生成器
     * @param serializerProvider  序列化提供器
     * @throws IOException 如果序列化过程中发生错误
     */
    @Override
    public void serialize(final String origin, final JsonGenerator jsonGenerator,
                          final SerializerProvider serializerProvider) throws IOException {
        if (origin == null) {
            jsonGenerator.writeNull();
            return;
        }

        // 根据敏感类型执行对应的脱敏操作
        switch (type) {
            case CHINESE_NAME:
                jsonGenerator.writeString(DesensitizedUtils.chineseName(origin));
                break;
            case ID_CARD:
                jsonGenerator.writeString(DesensitizedUtils.idCardNum(origin));
                break;
            case FIXED_PHONE:
                jsonGenerator.writeString(DesensitizedUtils.fixedPhone(origin));
                break;
            case MOBILE_PHONE:
                jsonGenerator.writeString(DesensitizedUtils.mobilePhone(origin));
                break;
            case ADDRESS:
                jsonGenerator.writeString(DesensitizedUtils.address(origin));
                break;
            case EMAIL:
                jsonGenerator.writeString(DesensitizedUtils.email(origin));
                break;
            case BANK_CARD:
                jsonGenerator.writeString(DesensitizedUtils.bankCard(origin));
                break;
            case PASSWORD:
                jsonGenerator.writeString(DesensitizedUtils.password(origin));
                break;
            case KEY:
                jsonGenerator.writeString(DesensitizedUtils.key(origin));
                break;
            case CUSTOM:
                jsonGenerator.writeString(DesensitizedUtils.desValue(origin, prefixNoMaskLen, suffixNoMaskLen, maskStr));
                break;
            default:
                throw new IllegalArgumentException("Unknown sensitive type enum: " + type);
        }
    }

    /**
     * 上下文化序列化器
     * <p>
     * 该方法用于根据字段的注解动态配置脱敏规则。
     * </p>
     *
     * @param serializerProvider 序列化提供器
     * @param beanProperty       字段属性
     * @return 返回合适的序列化器
     * @throws JsonMappingException 如果发生映射错误
     */
    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider serializerProvider,
                                              final BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty != null) {
            // 获取字段的脱敏注解
            Sensitive sensitive = beanProperty.getAnnotation(Sensitive.class);
            if (sensitive == null) {
                sensitive = beanProperty.getContextAnnotation(Sensitive.class);
            }

            if (sensitive != null) {
                // 根据注解的配置创建新的序列化器
                return new SensitiveSerialize(sensitive.type(), sensitive.prefixNoMaskLen(),
                        sensitive.suffixNoMaskLen(), sensitive.maskStr());
            }
        }

        // 如果没有脱敏注解，则使用默认的序列化器
        return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
    }
}
