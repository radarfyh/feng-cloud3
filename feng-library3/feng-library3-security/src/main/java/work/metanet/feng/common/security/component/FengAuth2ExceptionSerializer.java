package work.metanet.feng.common.security.component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.core.constant.enums.BusinessEnum;
import work.metanet.feng.common.security.exception.FengAuth2Exception;

import java.io.IOException;

/**
 * OAuth2 异常格式化处理器
 * <p>
 * 该类用于将 FengAuth2Exception 对象格式化为标准的 JSON 输出，
 * 输出格式包括 "code"、"msg" 和 "data" 字段，其中：
 * <ul>
 *   <li>"code" 表示错误代码，固定值为 CommonConstants.FAIL；</li>
 *   <li>"msg" 表示异常消息；</li>
 *   <li>"data" 表示具体错误码。</li>
 * </ul>
 * </p>
 */
public class FengAuth2ExceptionSerializer extends StdSerializer<FengAuth2Exception> {

    /**
     * 构造函数，指定需要序列化的异常类型为 FengAuth2Exception
     */
    public FengAuth2ExceptionSerializer() {
        super(FengAuth2Exception.class);
    }

    /**
     * 序列化 FengAuth2Exception 对象
     * <p>
     * 该方法将 FengAuth2Exception 对象序列化为 JSON 格式，其中：
     * <ul>
     *   <li>"code": 固定值为 CommonConstants.FAIL</li>
     *   <li>"msg": 异常消息</li>
     *   <li>"data": 异常错误代码</li>
     * </ul>
     * </p>
     *
     * @param value 要序列化的 FengAuth2Exception 对象
     * @param gen JSON 生成器
     * @param provider 序列化提供者
     * @throws IOException 如果在写入 JSON 时发生 I/O 异常
     */
    @Override
    public void serialize(FengAuth2Exception value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeObjectField("code", CommonConstants.FAIL);
        gen.writeStringField("msg", value.getMessage());
        gen.writeStringField("data", value.getErrorCode());
        gen.writeEndObject();
    }
}
