package work.metanet.feng.common.core.util;

import work.metanet.feng.common.core.config.TenantOfHeader;

/**
 * 字符串解析器接口
 * <p>
 * 该接口用于处理与字符串相关的操作，方便模块之间的解耦。
 * </p>
 */
public interface KeyStrResolver {

    /**
     * 字符串分割处理
     * <p>
     * 该方法接收一个字符串和分隔符，并根据分隔符将字符串进行加工或分割，返回处理后的字符串。
     * </p>
     *
     * @param in    输入字符串
     * @param split 分割符
     * @return 处理后的字符串
     */
    String extract(String in, String split);

    /**
     * 获取唯一标识符
     * <p>
     * 该方法返回模块或系统的唯一标识符。
     * </p>
     *
     * @return 模块或系统的唯一标识符
     */
    String key();
    
    /**
     * 获取客户端标识
     * <p>
     * 该方法返回客户端的标识符，通常用于标识客户端实例。
     * </p>
     *
     * @return 客户端标识符
     */
    String getClientId();

    /**
     * 获取用户名
     * <p>
     * 该方法返回当前操作的用户名。
     * </p>
     *
     * @return 当前用户的用户名
     */
    String getUsername();
}
