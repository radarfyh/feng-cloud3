package ltd.huntinginfo.feng.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import ltd.huntinginfo.feng.center.api.entity.UmpStatusCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 消息状态码表Mapper接口
 */
@Mapper
public interface UmpStatusCodeMapper extends BaseMapper<UmpStatusCode> {

    /**
     * 根据状态码查询
     *
     * @param statusCode 状态码
     * @return 状态码实体
     */
    UmpStatusCode selectByStatusCode(@Param("statusCode") String statusCode);

    /**
     * 分页查询状态码列表
     *
     * @param page 分页参数
     * @param statusCode 状态码（可选）
     * @param statusName 状态名称（可选）
     * @param category 分类（可选）
     * @param status 状态（可选）
     * @param parentCode 父状态码（可选）
     * @param isFinal 是否为最终状态（可选）
     * @return 分页结果
     */
    IPage<UmpStatusCode> selectStatusCodePage(IPage<UmpStatusCode> page,
                                             @Param("statusCode") String statusCode,
                                             @Param("statusName") String statusName,
                                             @Param("category") String category,
                                             @Param("status") Integer status,
                                             @Param("parentCode") String parentCode,
                                             @Param("isFinal") Integer isFinal);

    /**
     * 根据分类查询状态码列表
     *
     * @param category 分类
     * @param status 状态（可选）
     * @return 状态码列表
     */
    List<UmpStatusCode> selectByCategory(@Param("category") String category,
                                        @Param("status") Integer status);

    /**
     * 根据父状态码查询子状态码列表
     *
     * @param parentCode 父状态码
     * @param status 状态（可选）
     * @return 子状态码列表
     */
    List<UmpStatusCode> selectByParentCode(@Param("parentCode") String parentCode,
                                          @Param("status") Integer status);

    /**
     * 查询所有启用的状态码
     *
     * @return 启用的状态码列表
     */
    List<UmpStatusCode> selectAllEnabled();

    /**
     * 批量更新状态码状态
     *
     * @param ids 状态码ID列表
     * @param status 目标状态
     * @return 更新条数
     */
    int batchUpdateStatus(@Param("ids") List<String> ids,
                         @Param("status") Integer status);

    /**
     * 更新状态码信息
     *
     * @param id 状态码ID
     * @param statusName 状态名称
     * @param statusDesc 状态描述
     * @param sortOrder 排序
     * @param isFinal 是否为最终状态
     * @param canRetry 是否可重试
     * @param status 状态
     * @return 更新条数
     */
    int updateStatusCode(@Param("id") String id,
                        @Param("statusName") String statusName,
                        @Param("statusDesc") String statusDesc,
                        @Param("sortOrder") Integer sortOrder,
                        @Param("isFinal") Integer isFinal,
                        @Param("canRetry") Integer canRetry,
                        @Param("status") Integer status);

    /**
     * 获取状态码统计信息
     *
     * @return 统计结果
     */
    Map<String, Object> selectStatusCodeStatistics();

    /**
     * 检查状态码是否存在
     *
     * @param statusCode 状态码
     * @return 是否存在
     */
    boolean existsByStatusCode(@Param("statusCode") String statusCode);

    /**
     * 获取状态码层级树
     *
     * @param category 分类（可选）
     * @param status 状态（可选）
     * @return 状态码层级树
     */
    List<Map<String, Object>> selectStatusCodeTree(@Param("category") String category,
                                                  @Param("status") Integer status);

    /**
     * 根据分类和状态获取状态码映射
     *
     * @param category 分类
     * @param status 状态
     * @return 状态码映射
     */
    Map<String, String> selectStatusCodeMap(@Param("category") String category,
                                           @Param("status") Integer status);
}