package work.metanet.feng.common.data.resolver;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import work.metanet.feng.common.core.constant.SecurityConstants;
import work.metanet.feng.common.core.util.SpringContextHolder;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import work.metanet.feng.admin.api.feign.RemoteParamService;

/**
 * 系统参数配置解析器
 * <p>
 * 该类提供了根据键值对查询系统参数配置的功能。支持根据配置键查询对应的配置值，并且支持返回默认值。
 * </p>
 */
@UtilityClass
@Slf4j
public class ParamResolver {

    /**
     * 根据 key 查询 Long 类型的配置值
     *
     * @param key       配置键
     * @param defaultVal 默认值
     * @return 配置值，若没有配置值则返回默认值
     */
    public Long getLong(String key, Long... defaultVal) {
        return checkAndGet(key, Long.class, defaultVal);
    }

    /**
     * 根据 key 查询 String 类型的配置值
     *
     * @param key       配置键
     * @param defaultVal 默认值
     * @return 配置值，若没有配置值则返回默认值
     */
    public String getStr(String key, String... defaultVal) {
        return checkAndGet(key, String.class, defaultVal);
    }

    /**
     * 根据 key 查询配置值，如果没有查询到，则返回默认值。
     *
     * @param key         配置键
     * @param clazz       配置值类型
     * @param defaultVal  默认值
     * @param <T>         配置值的类型
     * @return 配置值
     */
    private <T> T checkAndGet(String key, Class<T> clazz, T... defaultVal) {
        // 校验 key 是否为空或 null
        if (StrUtil.isBlank(key)) {
            log.warn("参数 key 不能为空");
            return null;
        }

        // 校验 defaultVal 是否有效
        if (defaultVal.length > 1) {
            log.warn("默认值参数传入错误，默认值最多只能传入一个");
            return null;
        }

        RemoteParamService remoteParamService = SpringContextHolder.getBean(RemoteParamService.class);

        // 调用远程服务获取配置值
        String result = remoteParamService.getByKey(key, SecurityConstants.FROM_IN).getData();

        if (StrUtil.isNotBlank(result)) {
            // 将查询到的配置值转换为指定类型并返回
            return Convert.convert(clazz, result);
        }

        // 若配置值为空，返回默认值
        if (defaultVal.length == 1) {
            return defaultVal[0];
        }

        // 如果没有找到配置值，也没有默认值则返回 null
        return null;
    }
}
