package work.metanet.feng.common.core.util;

import work.metanet.feng.common.core.constant.CommonConstants;
import io.swagger.v3.oas.annotations.media.Schema;
//
//import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 响应信息主体
 * <p>
 * 该类用于封装API响应的统一格式，包括响应码、消息和数据。
 * </p>
 *
 * @param <T> 数据类型
 */
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Schema(name = "响应消息",description = "封装API响应的统一格式，包括响应码、消息和数据")
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应代码：成功=0，失败=1
     */
    @Getter
    @Setter
    @Schema(description = "响应代码：成功=0，失败=1")
    private int code;

    /**
     * 响应信息
     */
    @Getter
    @Setter
    @Schema(description = "响应信息")
    private String msg;

    /**
     * 响应数据
     */
    @Getter
    @Setter
    @Schema(description = "响应数据")
    private T data;

    /**
     * 构造成功的响应（无数据）
     *
     * @param <T> 泛型类型
     * @return 成功响应
     */
    public static <T> R<T> ok() {
        return restResult(null, CommonConstants.SUCCESS, null);
    }

    /**
     * 构造成功的响应（带数据）
     *
     * @param data 返回的数据
     * @param <T>  泛型类型
     * @return 成功响应
     */
    public static <T> R<T> ok(T data) {
        return restResult(data, CommonConstants.SUCCESS, null);
    }

    /**
     * 构造成功的响应（带数据和自定义消息）
     *
     * @param data 返回的数据
     * @param msg  自定义消息
     * @param <T>  泛型类型
     * @return 成功响应
     */
    public static <T> R<T> ok(T data, String msg) {
        return restResult(data, CommonConstants.SUCCESS, msg);
    }

    /**
     * 构造成功的响应（带响应码和自定义消息）
     *
     * @param code 自定义响应码
     * @param msg  自定义消息
     * @param <T>  泛型类型
     * @return 成功响应
     */
    public static <T> R<T> ok(int code, String msg) {
        return restResult(null, code, msg);
    }

    /**
     * 构造失败的响应（无数据）
     *
     * @param <T> 泛型类型
     * @return 失败响应
     */
    public static <T> R<T> failed() {
        return restResult(null, CommonConstants.FAIL, null);
    }

    /**
     * 构造失败的响应（带自定义消息）
     *
     * @param msg 失败消息
     * @param <T> 泛型类型
     * @return 失败响应
     */
    public static <T> R<T> failed(String msg) {
        return restResult(null, CommonConstants.FAIL, msg);
    }

    /**
     * 构造失败的响应（带响应码和自定义消息）
     *
     * @param code 响应码
     * @param msg  失败消息
     * @param <T>  泛型类型
     * @return 失败响应
     */
    public static <T> R<T> failed(int code, String msg) {
        return restResult(null, code, msg);
    }

    /**
     * 构造失败的响应（带数据）
     *
     * @param data 失败时返回的数据
     * @param <T>  泛型类型
     * @return 失败响应
     */
    public static <T> R<T> failed(T data) {
        return restResult(data, CommonConstants.FAIL, null);
    }

    /**
     * 构造失败的响应（带数据和自定义消息）
     *
     * @param data 失败时返回的数据
     * @param msg  失败时的自定义消息
     * @param <T>  泛型类型
     * @return 失败响应
     */
    public static <T> R<T> failed(T data, String msg) {
        return restResult(data, CommonConstants.FAIL, msg);
    }

    /**
     * 构造标准响应结果
     * <p>
     * 该方法用于生成一个带有响应码、消息和数据的响应。
     * </p>
     *
     * @param data 返回的数据
     * @param code 响应码
     * @param msg  响应消息
     * @param <T>  泛型类型
     * @return 响应结果
     */
    static <T> R<T> restResult(T data, int code, String msg) {
        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }
}
