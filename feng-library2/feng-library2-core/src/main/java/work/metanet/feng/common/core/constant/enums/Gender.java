package work.metanet.feng.common.core.constant.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

/**
 * 性别枚举
 * <p>
 * 该枚举类定义了性别数据字典。
 * </p>
 * 
 * @author EdisonFeng
 * @since 2025/5/31
 */
@Getter
public enum Gender implements IEnum<String> {
    // 初始默认值
    MALE("1", "男"),
    FEMALE("2", "女"),
    UNKNOWN("3", "未知");

    private static final Map<String, Gender> CACHE = new ConcurrentHashMap<>();
    private static volatile boolean initialized = false;

    private final String code;
    private final String name;

    static {
        resetToDefault();
    }

    Gender(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 重置为默认值
     */
    public static synchronized void resetToDefault() {
        CACHE.clear();
        Arrays.stream(values()).forEach(e -> CACHE.put(e.code, e));
        initialized = true;
    }

    /**
     * 动态更新枚举值（仅更新缓存，不创建新枚举实例）
     */
    public static synchronized void updateValues(Map<String, String> items) {
        CACHE.clear();
        items.forEach((code, name) -> {
            // 优先匹配已有枚举实例
            Optional<Gender> existing = Arrays.stream(values())
                .filter(e -> e.code.equals(code))
                .findFirst();
            
            if (existing.isPresent()) {
                CACHE.put(code, existing.get());
            } else {
                // 对于不在预定义枚举中的值，使用UNKNOWN作为占位符
                CACHE.put(code, UNKNOWN);
            }
        });
        initialized = true;
    }

    @Override
    public String getValue() {
        return this.code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static Gender fromCode(String code) {
        if (!initialized) {
            resetToDefault();
        }
        return CACHE.getOrDefault(code, UNKNOWN);
    }

    public static List<Gender> valuesDynamic() {
        return new ArrayList<>(CACHE.values());
    }

    public static String getNameByCode(String code) {
        Gender gender = fromCode(code);
        // 对于动态值，返回缓存中的名称（如果是UNKNOWN占位符则返回原始名称）
        if (gender == UNKNOWN && CACHE.containsKey(code)) {
            return CACHE.get(code).name;
        }
        return gender.getName();
    }
}
