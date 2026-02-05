package work.metanet.feng.common.core.util;

/**
 * 校验类型，分组验证规则
 */
public class ValidGroup {

    /**
     * 插入组
     *
     * @author edison
     * @date 2022/4/26
     */
    public static interface Save {
        // 保存时的验证规则
    }

    /**
     * 编辑组
     *
     * @author edison
     * @date 2022/4/26
     */
    public static interface Update {
        // 更新时的验证规则
    }

    /**
     * 删除组
     *
     * @author edison
     * @date 2022/4/26
     */
    public static interface Delete {
        // 删除时的验证规则
    }

    /**
     * 激活组
     *
     * @author edison
     * @date 2022/4/26
     */
    public static interface Activate {
        // 激活时的验证规则
    }

    /**
     * 禁用组
     *
     * @author edison
     * @date 2022/4/26
     */
    public static interface Deactivate {
        // 禁用时的验证规则
    }
}
