package work.metanet.feng.common.core.constant;

/**
 * Redisson 缓存常量
 * <p>
 * 该接口定义了与 Redisson 相关的缓存常量，用于标识支付订单等缓存键值。
 * </p>
 */
public interface RedissonConstants {

    /**
     * 支付订单缓存键
     * <p>
     * 该常量用于标识支付订单相关的缓存键值。
     * </p>
     */
    String PAY_ORDER = "payOrder";
}
