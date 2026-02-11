package ltd.huntinginfo.feng.center.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.huntinginfo.feng.center.api.entity.UmpAppPermission;
import ltd.huntinginfo.feng.center.api.dto.PermissionQueryDTO;
import ltd.huntinginfo.feng.center.api.vo.PermissionDetailVO;
import ltd.huntinginfo.feng.center.api.vo.PermissionPageVO;
import ltd.huntinginfo.feng.center.api.vo.PermissionStatisticsVO;

import java.util.List;
import java.util.Map;

/**
 * 应用权限表服务接口
 */
public interface UmpAppPermissionService extends IService<UmpAppPermission> {

    /**
     * 创建应用权限
     *
     * @param appKey 应用标识
     * @param resourceCode 资源代码
     * @param resourceName 资源名称
     * @param operation 操作类型
     * @return 权限ID
     */
    String createPermission(String appKey, String resourceCode, String resourceName, String operation);

    /**
     * 批量创建应用权限
     *
     * @param appKey 应用标识
     * @param permissions 权限列表（包含resourceCode, resourceName, operation）
     * @return 成功创建数量
     */
    int batchCreatePermissions(String appKey, List<Map<String, String>> permissions);

    /**
     * 更新应用权限
     *
     * @param permissionId 权限ID
     * @param resourceName 资源名称
     * @param operation 操作类型
     * @param status 状态
     * @return 是否成功
     */
    boolean updatePermission(String permissionId, String resourceName, String operation, Integer status);

    /**
     * 根据应用标识和资源代码查询权限
     *
     * @param appKey 应用标识
     * @param resourceCode 资源代码
     * @return 权限详情VO
     */
    PermissionDetailVO getPermissionByKeyAndResource(String appKey, String resourceCode);

    /**
     * 分页查询应用权限
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    Page<PermissionPageVO> queryPermissionPage(PermissionQueryDTO queryDTO);

    /**
     * 根据应用标识查询权限列表
     *
     * @param appKey 应用标识
     * @return 权限列表
     */
    List<PermissionDetailVO> getPermissionsByAppKey(String appKey);

    /**
     * 查询可用的应用权限列表
     *
     * @param appKey 应用标识
     * @return 可用的权限列表
     */
    List<PermissionDetailVO> getAvailablePermissions(String appKey);

    /**
     * 启用权限
     *
     * @param permissionId 权限ID
     * @return 是否成功
     */
    boolean enablePermission(String permissionId);

    /**
     * 禁用权限
     *
     * @param permissionId 权限ID
     * @return 是否成功
     */
    boolean disablePermission(String permissionId);

    /**
     * 批量启用权限
     *
     * @param permissionIds 权限ID列表
     * @return 成功启用数量
     */
    int batchEnablePermissions(List<String> permissionIds);

    /**
     * 批量禁用权限
     *
     * @param permissionIds 权限ID列表
     * @return 成功禁用数量
     */
    int batchDisablePermissions(List<String> permissionIds);

    /**
     * 检查应用是否有权限访问资源
     *
     * @param appKey 应用标识
     * @param resourceCode 资源代码
     * @param operation 操作类型
     * @return 是否有权限
     */
    boolean checkPermission(String appKey, String resourceCode, String operation);

    /**
     * 获取应用权限统计信息
     *
     * @return 统计信息VO
     */
    PermissionStatisticsVO getPermissionStatistics();

    /**
     * 逻辑删除权限
     *
     * @param permissionId 权限ID
     * @return 是否成功
     */
    boolean deletePermission(String permissionId);

    /**
     * 批量删除权限
     *
     * @param permissionIds 权限ID列表
     * @return 成功删除数量
     */
    int batchDeletePermissions(List<String> permissionIds);

    /**
     * 根据应用标识删除所有权限
     *
     * @param appKey 应用标识
     * @return 删除数量
     */
    int deletePermissionsByAppKey(String appKey);

    /**
     * 复制权限到其他应用
     *
     * @param sourceAppKey 源应用标识
     * @param targetAppKey 目标应用标识
     * @return 复制成功的权限数量
     */
    int copyPermissionsToApp(String sourceAppKey, String targetAppKey);

    /**
     * 获取应用的资源树
     *
     * @param appKey 应用标识
     * @return 资源树结构
     */
    Map<String, Object> getResourceTree(String appKey);

    /**
     * 验证操作权限
     *
     * @param appKey 应用标识
     * @param resourceCode 资源代码
     * @param operation 操作类型
     * @return 是否验证通过
     */
    boolean validateOperation(String appKey, String resourceCode, String operation);
}