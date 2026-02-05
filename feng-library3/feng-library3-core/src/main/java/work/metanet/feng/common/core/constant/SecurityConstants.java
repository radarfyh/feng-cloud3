package work.metanet.feng.common.core.constant;

/**
 * 安全相关常量
 * <p>
 * 该接口定义了与安全性、OAuth2、加密、客户端等相关的常量。
 * 包括 token、验证码、OAuth2 授权 URL、客户端相关配置等。
 * </p>
 */
public interface SecurityConstants {

    /**
     * 启动时是否检查Inner注解的安全性
     */
    boolean INNER_CHECK = true;

    /**
     * 刷新 token
     */
    String REFRESH_TOKEN = "refresh_token";

    /**
     * 认证 token
     */
    String ACCESS_TOKEN = "access_token";

    /**
     * 验证码有效期（单位：秒）
     */
    int CODE_TIME = 60;

    /**
     * 验证码长度
     */
    String CODE_SIZE = "4";

    /**
     * 角色前缀
     */
    String ROLE = "ROLE_";

    /**
     * 项目的统一前缀
     */
    String FENG_PREFIX = "feng_";

    /**
     * token 相关前缀
     */
    String TOKEN_PREFIX = "token:";

    /**
     * OAuth 相关前缀
     */
    String OAUTH_PREFIX = "oauth:";

    /**
     * 授权码模式 code key 前缀
     */
    String OAUTH_CODE_PREFIX = "oauth:code:";

    /**
     * 项目的 license 信息
     */
    String FENG_LICENSE = "made by feng";

    /**
     * Header中key=FROM的值value
     */
    String FROM_IN = "Y";

    /**
     * 来源标志，作为key加到Header中
     */
    String FROM = "from";

    /**
     * 移动端授权标志
     */
    String GRANT_MOBILE = "mobile";

    /**
     * OAuth Token 请求 URL
     */
    String OAUTH_TOKEN_URL = "/oauth/token";

    /**
     * 手机号登录 URL
     */
    String SMS_TOKEN_URL = "/mobile/token/sms";

    /**
     * 社交登录 URL
     */
    String SOCIAL_TOKEN_URL = "/mobile/token/social";

    /**
     * 自定义登录 URL
     */
    String MOBILE_TOKEN_URL = "/mobile/token/*";

    /**
     * 微信获取 OPENID 的 URL
     */
    String WX_AUTHORIZATION_CODE_URL = "https://api.weixin.qq.com/sns/oauth2/access_token"
            + "?appid=%s&secret=%s&code=%s&grant_type=authorization_code";

    /**
     * 微信小程序获取 OPENID 的 URL
     */
    String MINI_APP_AUTHORIZATION_CODE_URL = "https://api.weixin.qq.com/sns/jscode2session"
            + "?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";

    /**
     * 码云获取 token 的 URL
     */
    String GITEE_AUTHORIZATION_CODE_URL = "https://gitee.com/oauth/token?grant_type="
            + "authorization_code&code=%S&client_id=%s&redirect_uri=" + "%s&client_secret=%s";

    /**
     * 开源中国获取 token 的 URL
     */
    String OSC_AUTHORIZATION_CODE_URL = "https://www.oschina.net/action/openapi/token";

    /**
     * 码云获取用户信息的 URL
     */
    String GITEE_USER_INFO_URL = "https://gitee.com/api/v5/user?access_token=%s";

    /**
     * 开源中国获取用户信息的 URL
     */
    String OSC_USER_INFO_URL = "https://www.oschina.net/action/openapi/user?access_token=%s&dataType=json";

    /**
     * bcrypt 加密的特征码
     */
    String BCRYPT = "{bcrypt}";

    /**
     * sys_oauth_client_details 表的字段，不包括 client_id 和 client_secret
     */
    String CLIENT_FIELDS = "client_id, CONCAT('{noop}',client_secret) as client_secret, resource_ids, scope, "
            + "authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, "
            + "refresh_token_validity, additional_information, autoapprove";

    /**
     * JdbcClientDetailsService 查询语句
     */
    String BASE_FIND_STATEMENT = "select " + CLIENT_FIELDS + " from sys_oauth_client_details";

    /**
     * 按条件 client_id 查询
     */
    String DEFAULT_SELECT_STATEMENT = BASE_FIND_STATEMENT + " where client_id = ? and del_flag = '0' and organ_code = %s";

    /**
     * 资源服务器默认配置 bean 名称
     */
    String RESOURCE_SERVER_CONFIGURER = "resourceServerConfigurerAdapter";

    /**
     * 客户端模式
     */
    String CLIENT_CREDENTIALS = "client_credentials";

    /**
     * 客户端编号
     */
    String CLIENT_ID = "client_id";

    /**
     * 用户 ID 字段
     */
    String DETAILS_USER_ID = "id";

    /**
     * 用户名字段
     */
    String DETAILS_USERNAME = "username";

    /**
     * 用户基本信息字段
     */
    String DETAILS_USER = "user_info";

    /**
     * 用户手机号字段
     */
    String DETAILS_PHONE = "phone";

    /**
     * 用户头像字段
     */
    String DETAILS_AVATAR = "avatar";

    /**
     * 用户部门字段
     */
    String DETAILS_DEPT_ID = "deptId";

    /**
     * 科室编码字段
     */
    String DETAILS_DEPT_CODE = "deptCode";

    /**
     * 是否首次登录字段：0-否 / 1-是，默认1
     */
    String DETAILS_FIRST_LOGIN = "firstLogin";

    /**
     * 租户ID字段
     */
    String DETAILS_TENANT_ID = "tenantId";

    /**
     * 机构编码字段
     */
    String DETAILS_ORG_ID = "organCode";

    /**
     * 协议字段
     */
    String DETAILS_LICENSE = "license";

    /**
     * 激活字段，兼容外围系统接入
     */
    String ACTIVE = "active";

    /**
     * AES 加密
     */
    String AES = "aes";

    /**
     * 是否开启 Redis JSON 格式化
     */
    boolean JSON_FORMAT = false;
    
    String BEARER_TOKEN_TYPE = "Bearer ";

}
