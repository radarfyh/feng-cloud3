package work.metanet.feng.common.core.util;

import cn.hutool.core.util.ObjectUtil;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.core.constant.enums.BusinessEnum;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 响应数据操作工具类
 * <p>
 * 该类封装了对响应对象 {@link R} 的常见操作，如断言、转换、条件消费等。
 * </p>
 *
 * @param <T> 数据类型
 */
public class RetOps<T> {

    /** 状态码为成功 */
    public static final Predicate<R<?>> CODE_SUCCESS = r -> CommonConstants.SUCCESS == r.getCode();

    /** 数据有值 */
    public static final Predicate<R<?>> HAS_DATA = r -> ObjectUtil.isNotEmpty(r.getData());

    /** 数据有值,并且包含元素 */
    public static final Predicate<R<?>> HAS_ELEMENT = r -> ObjectUtil.isNotEmpty(r.getData());

    /** 状态码为成功并且有值 */
    public static final Predicate<R<?>> DATA_AVAILABLE = CODE_SUCCESS.and(HAS_DATA);

    private final R<T> original;

    // ~ 初始化
    // ===================================================================================================

    private RetOps(R<T> original) {
        this.original = original;
    }

    public static <T> RetOps<T> of(R<T> original) {
        return new RetOps<>(Objects.requireNonNull(original, "Response object cannot be null"));
    }

    // ~ 杂项方法
    // ===================================================================================================

    /**
     * 获取原始响应对象
     * @return 返回原始的 {@link R} 对象
     */
    public R<T> peek() {
        return original;
    }

    /**
     * 获取响应的 {@code code}
     * @return 响应的状态码
     */
    public int getCode() {
        return original.getCode();
    }

    /**
     * 获取响应的 {@code data} 数据
     * @return 返回 Optional 包装的 data
     */
    public Optional<T> getData() {
        return Optional.ofNullable(original.getData());
    }

    /**
     * 获取条件满足时的 {@code data} 数据
     * @param predicate 断言函数
     * @return 如果断言成功返回 {@code data}，否则返回 {@link Optional#empty()}
     */
    public Optional<T> getDataIf(Predicate<? super R<?>> predicate) {
        return predicate.test(original) ? getData() : Optional.empty();
    }

    /**
     * 获取响应的 {@code msg} 信息
     * @return 返回 Optional 包装的 msg
     */
    public Optional<String> getMsg() {
        return Optional.ofNullable(original.getMsg());
    }

    /**
     * 判断 {@code code} 是否等于指定值
     * @param value 预期的值
     * @return 如果相等返回 true，否则 false
     */
    public boolean codeEquals(int value) {
        return original.getCode() == value;
    }

    /**
     * 判断 {@code code} 是否不等于指定值
     * @param value 预期的值
     * @return 如果不相等返回 true，否则 false
     */
    public boolean codeNotEquals(int value) {
        return !codeEquals(value);
    }

    /**
     * 判断响应是否成功
     * @return 如果成功返回 true，否则 false
     */
    public boolean isSuccess() {
        return codeEquals(CommonConstants.SUCCESS);
    }

    /**
     * 判断响应是否失败
     * @return 如果失败返回 true，否则 false
     */
    public boolean notSuccess() {
        return !isSuccess();
    }

    // ~ 链式操作
    // ===================================================================================================

    /**
     * 断言 {@code code} 是否等于预期值
     * @param expect 预期值
     * @param func   断言失败时创建异常的函数
     * @param <Ex>   异常类型
     * @return 当前实例，支持链式操作
     * @throws Ex 断言失败时抛出
     */
    public <Ex extends Exception> RetOps<T> assertCode(int expect, Function<? super R<T>, ? extends Ex> func) throws Ex {
        if (codeNotEquals(expect)) {
            throw func.apply(original);
        }
        return this;
    }

    /**
     * 断言响应成功
     * @param func   断言失败时创建异常的函数
     * @param <Ex>   异常类型
     * @return 当前实例，支持链式操作
     * @throws Ex 断言失败时抛出
     */
    public <Ex extends Exception> RetOps<T> assertSuccess(Function<? super R<T>, ? extends Ex> func) throws Ex {
        return assertCode(CommonConstants.SUCCESS, func);
    }

    /**
     * 断言数据不为 null
     * @param func   断言失败时创建异常的函数
     * @param <Ex>   异常类型
     * @return 当前实例，支持链式操作
     * @throws Ex 断言失败时抛出
     */
    public <Ex extends Exception> RetOps<T> assertDataNotNull(Function<? super R<T>, ? extends Ex> func) throws Ex {
        if (Objects.isNull(original.getData())) {
            throw func.apply(original);
        }
        return this;
    }

    /**
     * 断言数据不为空
     * @param func   断言失败时创建异常的函数
     * @param <Ex>   异常类型
     * @return 当前实例，支持链式操作
     * @throws Ex 断言失败时抛出
     */
    public <Ex extends Exception> RetOps<T> assertDataNotEmpty(Function<? super R<T>, ? extends Ex> func) throws Ex {
        if (ObjectUtil.isEmpty(original.getData())) {
            throw func.apply(original);
        }
        return this;
    }

    /**
     * 数据转换
     * @param mapper 数据转换函数
     * @param <U> 转换后的数据类型
     * @return 新的实例，支持链式操作
     */
    public <U> RetOps<U> map(Function<? super T, ? extends U> mapper) {
        R<U> result = R.restResult(mapper.apply(original.getData()), original.getCode(), original.getMsg());
        return of(result);
    }

    /**
     * 数据转换，只有在满足条件时
     * @param predicate 断言函数
     * @param mapper 数据转换函数
     * @param <U> 转换后的数据类型
     * @return 新的实例，支持链式操作
     */
    public <U> RetOps<U> mapIf(Predicate<? super R<T>> predicate, Function<? super T, ? extends U> mapper) {
        R<U> result = predicate.test(original)
                ? R.restResult(mapper.apply(original.getData()), original.getCode(), original.getMsg())
                : R.restResult(null, original.getCode(), original.getMsg());
        return of(result);
    }

    // ~ 数据消费
    // ===================================================================================================

    /**
     * 消费数据，保证数据可用
     * @param consumer 数据消费函数
     */
    public void useData(Consumer<? super T> consumer) {
        Optional.ofNullable(original.getData()).ifPresent(consumer);
    }

    /**
     * 根据错误代码消费数据
     * @param consumer 数据消费函数
     * @param codes 错误代码集合，匹配任意一个则调用消费函数
     */
    public void useDataOnCode(Consumer<? super T> consumer, int... codes) {
        if (Arrays.stream(codes).anyMatch(c -> original.getCode() == c)) {
            useData(consumer);
        }
    }

    /**
     * 成功时消费数据
     * @param consumer 数据消费函数
     */
    public void useDataIfSuccess(Consumer<? super T> consumer) {
        if (isSuccess()) {
            useData(consumer);
        }
    }

    /**
     * 条件消费数据
     * @param predicate 断言函数
     * @param consumer 数据消费函数，断言函数返回 true 时调用
     */
    public void useDataIf(Predicate<? super R<T>> predicate, Consumer<? super T> consumer) {
        if (predicate.test(original)) {
            useData(consumer);
        }
    }
}
