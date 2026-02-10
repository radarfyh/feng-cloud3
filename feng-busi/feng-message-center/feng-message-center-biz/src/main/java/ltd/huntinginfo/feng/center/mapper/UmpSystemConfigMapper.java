package ltd.huntinginfo.feng.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import ltd.huntinginfo.feng.center.api.entity.UmpSystemConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 系统配置表Mapper接口
 */
@Mapper
public interface UmpSystemConfigMapper extends BaseMapper<UmpSystemConfig> {

    /**
     * 根据配置键查询配置
     *
     * @param configKey 配置键
     * @return 配置实体
     */
    UmpSystemConfig selectByConfigKey(@Param("configKey") String configKey);

    /**
     * 分页查询配置列表
     *
     * @param page 分页参数
     * @param configKey 配置键（可选）
     * @param configType 配置类型（可选）
     * @param category 配置类别（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    IPage<UmpSystemConfig> selectConfigPage(IPage<UmpSystemConfig> page,
                                           @Param("configKey") String configKey,
                                           @Param("configType") String configType,
                                           @Param("category") String category,
                                           @Param("status") Integer status);

    /**
     * 根据类别查询配置列表
     *
     * @param category 配置类别
     * @param status 状态（可选）
     * @return 配置列表
     */
    List<UmpSystemConfig> selectByCategory(@Param("category") String category,
                                          @Param("status") Integer status);

    /**
     * 根据配置类型查询配置列表
     *
     * @param configType 配置类型
     * @param status 状态（可选）
     * @return 配置列表
     */
    List<UmpSystemConfig> selectByConfigType(@Param("configType") String configType,
                                            @Param("status") Integer status);

    /**
     * 更新配置值
     *
     * @param configKey 配置键
     * @param configValue 配置值
     * @return 更新条数
     */
    int updateConfigValue(@Param("configKey") String configKey,
                         @Param("configValue") String configValue);

    /**
     * 批量更新配置状态
     *
     * @param ids 配置ID列表
     * @param status 目标状态
     * @return 更新条数
     */
    int batchUpdateStatus(@Param("ids") List<String> ids,
                         @Param("status") Integer status);

    /**
     * 获取配置统计信息
     *
     * @return 统计结果
     */
    Map<String, Object> selectConfigStatistics();

    /**
     * 检查配置键是否存在
     *
     * @param configKey 配置键
     * @return 是否存在
     */
    boolean existsByConfigKey(@Param("configKey") String configKey);

    /**
     * 根据配置键列表查询配置
     *
     * @param configKeys 配置键列表
     * @param status 状态（可选）
     * @return 配置列表
     */
    List<UmpSystemConfig> selectByConfigKeys(@Param("configKeys") List<String> configKeys,
                                            @Param("status") Integer status);

    /**
     * 获取配置键值映射
     *
     * @param category 类别（可选）
     * @param status 状态（可选）
     * @return 配置键值映射
     */
    Map<String, String> selectConfigMap(@Param("category") String category,
                                       @Param("status") Integer status);
}