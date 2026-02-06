package work.metanet.feng.common.core.constant;

/**
 * 服务名称常量
 * <p>
 * 该接口定义了微服务架构中的服务名称常量，用于标识不同的服务模块。
 * </p>
 */
public interface ServiceNameConstants {

    /**
     * 认证中心服务
     * <p>
     * 该常量表示认证服务模块的服务名称。
     * </p>
     */
    String AUTH_SERVICE = "feng-auth";

    /**
     * 用户管理服务
     * <p>
     * 该常量表示用户管理模块的服务名称。
     * </p>
     */
    String USER2_SERVICE = "feng-user3-biz";
    
    /**
     * 网关服务
     * <p>
     * 该常量表示网关模块的服务名称。
     * </p>
     */
    String GATEWAY_SERVICE = "feng-gateway";

    /**
     * 消息服务
     * <p>
     * 该常量表示消息处理模块的服务名称。
     * </p>
     */
    String MSG_SERVICE = "feng-msg-biz";
}
