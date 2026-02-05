package work.metanet.feng.common.data.mybatis;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import work.metanet.feng.common.core.constant.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ClassUtils;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * MybatisPlus 自动填充配置
 * <p>
 * 主要功能包括插入和更新操作时，自动填充审计字段、删除标记以及操作人信息。
 * </p>
 */
@Slf4j
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入操作时的字段自动填充
     *
     * @param metaObject 元数据对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("MybatisPlus开始进行插入操作填充...");

        LocalDateTime now = LocalDateTime.now();

        // 填充审计字段
        fillValIfNullByName("createTime", now, metaObject, false);
        fillValIfNullByName("updateTime", now, metaObject, false);
        fillValIfNullByName("createBy", getUserName(), metaObject, false);
        fillValIfNullByName("updateBy", getUserName(), metaObject, false);

        // 填充删除标记
        fillValIfNullByName("delFlag", CommonConstants.STATUS_NORMAL, metaObject, false);
    }

    /**
     * 更新操作时的字段自动填充
     *
     * @param metaObject 元数据对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("MybatisPlus开始进行更新操作填充...");

        // 填充更新时间和更新人字段
        fillValIfNullByName("updateTime", LocalDateTime.now(), metaObject, true);
        fillValIfNullByName("updateBy", getUserName(), metaObject, true);
    }

    /**
     * 根据字段名填充字段值
     *
     * @param fieldName 字段名称
     * @param fieldVal 字段值
     * @param metaObject 元数据对象
     * @param isCover 是否覆盖原值
     */
    private static void fillValIfNullByName(String fieldName, Object fieldVal, MetaObject metaObject, boolean isCover) {
        // 如果填充值为空，直接返回
        if (fieldVal == null) {
            return;
        }

        // 判断字段是否有对应的 setter 方法
        if (!metaObject.hasSetter(fieldName)) {
            return;
        }

        // 获取用户手动设置的值
        Object userSetValue = metaObject.getValue(fieldName);
        String setValueStr = String.valueOf(userSetValue);  // 使用 String.valueOf() 转换为字符串，避免使用默认字符集

        // 如果手动设置了值且不允许覆盖，则跳过
        if (StrUtil.isNotBlank(setValueStr) && !isCover) {
            return;
        }

        // 获取字段的类型并判断是否可以赋值
        Class<?> getterType = metaObject.getGetterType(fieldName);
        if (ClassUtils.isAssignableValue(getterType, fieldVal)) {
            metaObject.setValue(fieldName, fieldVal);
        }
    }

    /**
     * 获取当前 Spring Security 用户的用户名
     *
     * @return 当前用户名，如果未登录则返回 null
     */
    private String getUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Optional.ofNullable(authentication).isPresent()) {
            return authentication.getName();
        }
        return null;  // 如果没有认证信息，则返回 null
    }
}
