package ltd.huntinginfo.feng.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import ltd.huntinginfo.feng.center.api.entity.UmpAppCredential;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 应用认证凭证表Mapper接口
 */
@Mapper
public interface UmpAppCredentialMapper extends BaseMapper<UmpAppCredential> {

    /**
     * 根据应用标识查询应用凭证
     *
     * @param appKey 应用标识
     * @return 应用凭证实体
     */
    UmpAppCredential selectByAppKey(@Param("appKey") String appKey);

    /**
     * 分页查询应用凭证列表
     *
     * @param page 分页参数
     * @param appName 应用名称（可选）
     * @param appType 应用类型（可选）
     * @param status 状态（可选）
     * @param appKey 应用标识（可选）
     * @return 分页结果
     */
    IPage<UmpAppCredential> selectAppPage(IPage<UmpAppCredential> page,
                                         @Param("appName") String appName,
                                         @Param("appType") String appType,
                                         @Param("status") Integer status,
                                         @Param("appKey") String appKey);

    /**
     * 根据状态查询应用凭证列表
     *
     * @param status 状态
     * @return 应用凭证列表
     */
    List<UmpAppCredential> selectByStatus(@Param("status") Integer status);

    /**
     * 查询可用的应用凭证列表
     *
     * @return 可用的应用凭证列表
     */
    List<UmpAppCredential> selectAvailableApps();

    /**
     * 批量更新应用状态
     *
     * @param ids 应用ID列表
     * @param status 目标状态
     * @return 更新条数
     */
    int batchUpdateStatus(@Param("ids") List<String> ids,
                         @Param("status") Integer status);

    /**
     * 更新应用密钥
     *
     * @param id 应用ID
     * @param appSecret 新的应用密钥
     * @param secretExpireTime 密钥过期时间
     * @return 更新条数
     */
    int updateAppSecret(@Param("id") String id,
                       @Param("appSecret") String appSecret,
                       @Param("secretExpireTime") LocalDateTime secretExpireTime);

    /**
     * 获取应用统计信息
     *
     * @return 统计结果
     */
    Map<String, Object> selectAppStatistics();

    /**
     * 检查应用标识是否存在
     *
     * @param appKey 应用标识
     * @return 是否存在
     */
    boolean existsByAppKey(@Param("appKey") String appKey);
}