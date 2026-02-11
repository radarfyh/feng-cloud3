package ltd.huntinginfo.feng.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import ltd.huntinginfo.feng.center.api.entity.UmpAppPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 应用权限表Mapper接口
 */
@Mapper
public interface UmpAppPermissionMapper extends BaseMapper<UmpAppPermission> {

    /**
     * 根据应用标识和资源代码查询权限
     *
     * @param appKey 应用标识
     * @param resourceCode 资源代码
     * @return 权限实体
     */
    UmpAppPermission selectByAppKeyAndResourceCode(@Param("appKey") String appKey, 
                                                  @Param("resourceCode") String resourceCode);

    /**
     * 分页查询应用权限列表
     *
     * @param page 分页参数
     * @param appKey 应用标识（可选）
     * @param resourceCode 资源代码（可选）
     * @param resourceName 资源名称（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    IPage<UmpAppPermission> selectPermissionPage(IPage<UmpAppPermission> page,
                                                @Param("appKey") String appKey,
                                                @Param("resourceCode") String resourceCode,
                                                @Param("resourceName") String resourceName,
                                                @Param("status") Integer status);

    /**
     * 根据应用标识查询权限列表
     *
     * @param appKey 应用标识
     * @return 权限列表
     */
    List<UmpAppPermission> selectByAppKey(@Param("appKey") String appKey);

    /**
     * 查询可用的应用权限列表
     *
     * @param appKey 应用标识
     * @return 可用的权限列表
     */
    List<UmpAppPermission> selectAvailablePermissions(@Param("appKey") String appKey);

    /**
     * 批量更新权限状态
     *
     * @param ids 权限ID列表
     * @param status 目标状态
     * @return 更新条数
     */
    int batchUpdateStatus(@Param("ids") List<String> ids,
                         @Param("status") Integer status);

    /**
     * 获取应用权限统计信息
     *
     * @return 统计结果
     */
    Map<String, Object> selectPermissionStatistics();

    /**
     * 检查权限是否存在
     *
     * @param appKey 应用标识
     * @param resourceCode 资源代码
     * @return 是否存在
     */
    boolean existsByAppKeyAndResourceCode(@Param("appKey") String appKey, 
                                         @Param("resourceCode") String resourceCode);

    /**
     * 批量插入权限
     *
     * @param permissions 权限列表
     * @return 插入条数
     */
    int batchInsert(@Param("list") List<UmpAppPermission> permissions);

    /**
     * 根据应用标识和操作类型查询权限
     *
     * @param appKey 应用标识
     * @param operation 操作类型
     * @return 权限列表
     */
    List<UmpAppPermission> selectByAppKeyAndOperation(@Param("appKey") String appKey, 
                                                     @Param("operation") String operation);

    /**
     * 获取应用所有资源代码
     *
     * @param appKey 应用标识
     * @return 资源代码列表
     */
    List<String> selectResourceCodesByAppKey(@Param("appKey") String appKey);
}