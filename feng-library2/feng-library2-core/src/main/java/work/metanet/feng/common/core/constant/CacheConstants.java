package work.metanet.feng.common.core.constant;

/**
 * 缓存的key常量
 * <p>
 * 该接口定义了应用中使用的所有缓存Key常量。
 * </p>
 */
public interface CacheConstants {

    /**
     * 全局缓存，在缓存名称上加上该前缀表示该缓存不区分租户。
     * 例如: 
     * {@code @Cacheable(value = CacheConstants.GLOBALLY + CacheConstants.MENU_DETAILS, key = "#roleId  + '_menu'", unless = "#result == null")}
     */
    String GLOBALLY = "global:";

    // 以下常量表示不同的缓存类型，应该有明确的分隔和统一的命名规范
    String FENG_TENANT = "feng_tenant";  // 统一租户缓存

    // 验证码缓存前缀
    String DEFAULT_CODE_KEY = "default_code_key:";

    // 菜单信息缓存
    String MENU_DETAILS = "menu_details";

    // 用户信息缓存
    String USER_DETAILS = "user_details";
    
    // 租户ID缓存键
    String OAUTH_ACCESS  = "oauth:access:token:";

    // 角色信息缓存
    String ROLE_DETAILS = "role_details";

    // 字典信息缓存
    String DICT_DETAILS = "dict_details";

    // 科室信息缓存
    String DEPARTMENT_DETAILS = "department_details";

    // oauth 客户端信息缓存
    String CLIENT_DETAILS_KEY = "feng_oauth:client:details";

    // spring boot admin 事件缓存key
    String EVENT_KEY = GLOBALLY + "event_key";

    // 路由缓存
    String ROUTE_KEY = GLOBALLY + "gateway_route_key";

    // 内存重载时间
    String ROUTE_JVM_RELOAD_TOPIC = "gateway_jvm_route_reload_topic";

    // redis 重新加载路由信息
    String ROUTE_REDIS_RELOAD_TOPIC = "user2_redis_route_reload_topic";

    // redis 重新加载客户端信息
    String CLIENT_REDIS_RELOAD_TOPIC = "user2_redis_client_reload_topic";

    // 公众号重新加载
    String MP_REDIS_RELOAD_TOPIC = "mp_redis_reload_topic";

    // 支付重新加载事件
    String PAY_REDIS_RELOAD_TOPIC = "pay_redis_reload_topic";

    // 参数缓存
    String PARAMS_DETAILS = "params_details";

    // 机构缓存 (不区分机构)
    String ORGAN_DETAILS = GLOBALLY + "organ_details";

    // 应用缓存 (不区分机构)
    String APPLICATION_DETAILS = GLOBALLY + "application_details";

    // 客户端配置缓存标志
    String CLIENT_FLAG = "client_config_flag";

    // 用户的基本信息缓存
    String USER_INFO = "user_info";

    // 登录错误次数缓存
    String LOGIN_ERROR_TIMES = "login_error_times";
}
