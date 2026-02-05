package work.metanet.feng.common.core.constant;

/**
 * 公共常量
 * <p>
 * 该接口定义了常用的常量，涵盖机构编码、状态标记、项目名、验证码配置等。
 * </p>
 */
public interface CommonConstants {
    /**
     * 租户头
     */
    String TENANT_HEADER_KEY = "J-Cat";
    /**
     * 租户ID
     */
    String TENANT_ID = "jCat";
    /**
     * 默认租户ID
     */
    String DEFAULT_TENANT_ID = "100";
    
    /**
     * 默认租户编码
     */
    String DEFAULT_TENANT_CODE = "default";
    
    /**
     * 租户头默认密钥
     */
    String DEFAULT_TENANT_CRYPT_KEY = "fengyonghua4java";
    /**
     * Header 中版本信息
     */
    String VERSION = "VERSION";

    /**
     * 机构编码
     */
    String ORGAN_CODE = "organCode";
    /**
     * 默认机构编码
     */
    String ORGAN_CODE_ADMIN = "F001";
    
    /**
     * 删除标记：1 - 删除
     */
    String STATUS_DEL = "1";

    /**
     * 删除标记：0 - 正常
     */
    String STATUS_NORMAL = "0";

    /**
     * 登录账户号禁用状态
     */
    String STATUS_USERNAME = "0";

    /**
     * 锁定标记：0 - 锁定
     */
    String STATUS_LOCK = "0";

    /**
     * 菜单树根节点
     */
    Integer MENU_TREE_ROOT_ID = 0;

    /**
     * 编码格式：UTF-8
     */
    String UTF8 = "UTF-8";

    /**
     * 前端工程名
     */
    String FRONT_END_PROJECT = "feng-ui";

    /**
     * 后端工程名
     */
    String BACK_END_PROJECT = "feng";

    /**
     * 公共参数
     */
    String FENG_PUBLIC_PARAM_KEY = "feng_public_param_key";

    /**
     * 成功标记
     */
    Integer SUCCESS = 0;

    /**
     * 失败标记
     */
    Integer FAIL = 1;

    /**
     * 默认公共存储桶（bucket）
     */
    String BUCKET_NAME = "feng-bucket";

    /**
     * 滑块验证码类型
     */
    String IMAGE_CODE_TYPE = "blockPuzzle";

    /**
     * 文字点选验证码类型
     */
    String WORD_CODE_TYPE = "clickWord";

    /**
     * 验证码开关标志
     */
    String CAPTCHA_FLAG = "captcha_flag";

    /**
     * 密码传输是否加密标志
     */
    String ENC_FLAG = "enc_flag";

    /**
     * 客户端允许同时在线的数量
     */
    String ONLINE_QUANTITY = "online_quantity";

    /**
     * Hutool-captcha 验证码字段标识
     */
    String CAPTCHA_CODE = "code";

    /**
     * Hutool-captcha 验证码统一标识
     */
    String CAPTCHA_UUID = "uuid";
    
    /**
     * UTF-8 编码
     */
    String UTF_8 = "utf-8";

    /**
     * 菜单类型：menu
     */
    String MENU_TYPE_MENU = "menu";

    /**
     * 菜单类型：button
     */
    String MENU_TYPE_BUTTON = "button";

    /**
     * 菜单：默认Icon图标
     */
    String MENU_ICON = "alert";

    String LAYOUT = "Layout";
    
    /**
     * 列表查询限制最大数量
     */
    String LIMIT_LIST_QUERY = "LIMIT 1000";
}
