package ltd.huntinginfo.feng.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 消息模板表Mapper接口
 */
@Mapper
public interface UmpMsgTemplateMapper extends BaseMapper<UmpMsgTemplate> {

    /**
     * 根据模板代码查询模板
     *
     * @param templateCode 模板代码
     * @return 模板实体
     */
    UmpMsgTemplate selectByTemplateCode(@Param("templateCode") String templateCode);

    /**
     * 分页查询模板列表
     *
     * @param page 分页参数
     * @param templateName 模板名称（可选）
     * @param templateType 模板类型（可选）
     * @param templateCode 模板代码（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    IPage<UmpMsgTemplate> selectTemplatePage(IPage<UmpMsgTemplate> page,
                                            @Param("templateName") String templateName,
                                            @Param("templateType") String templateType,
                                            @Param("templateCode") String templateCode,
                                            @Param("status") Integer status);

    /**
     * 根据模板类型查询模板列表
     *
     * @param templateType 模板类型
     * @param status 状态（可选）
     * @return 模板列表
     */
    List<UmpMsgTemplate> selectByTemplateType(@Param("templateType") String templateType,
                                             @Param("status") Integer status);

    /**
     * 查询所有启用的模板
     *
     * @return 启用的模板列表
     */
    List<UmpMsgTemplate> selectAllEnabled();

    /**
     * 批量更新模板状态
     *
     * @param ids 模板ID列表
     * @param status 目标状态
     * @return 更新条数
     */
    int batchUpdateStatus(@Param("ids") List<String> ids,
                         @Param("status") Integer status);

    /**
     * 更新模板信息
     *
     * @param id 模板ID
     * @param templateName 模板名称
     * @param titleTemplate 标题模板
     * @param contentTemplate 内容模板
     * @param variables 模板变量
     * @param defaultPriority 默认优先级
     * @param defaultPushMode 默认推送方式
     * @param defaultCallbackUrl 默认回调地址
     * @param status 状态
     * @return 更新条数
     */
    int updateTemplate(@Param("id") String id,
                      @Param("templateName") String templateName,
                      @Param("titleTemplate") String titleTemplate,
                      @Param("contentTemplate") String contentTemplate,
                      @Param("variables") Map<String, Object> variables,
                      @Param("defaultPriority") Integer defaultPriority,
                      @Param("defaultPushMode") String defaultPushMode,
                      @Param("defaultCallbackUrl") String defaultCallbackUrl,
                      @Param("status") Integer status);

    /**
     * 获取模板统计信息
     *
     * @return 统计结果
     */
    Map<String, Object> selectTemplateStatistics();

    /**
     * 检查模板代码是否存在
     *
     * @param templateCode 模板代码
     * @return 是否存在
     */
    boolean existsByTemplateCode(@Param("templateCode") String templateCode);

    /**
     * 根据模板代码列表查询模板
     *
     * @param templateCodes 模板代码列表
     * @param status 状态（可选）
     * @return 模板列表
     */
    List<UmpMsgTemplate> selectByTemplateCodes(@Param("templateCodes") List<String> templateCodes,
                                              @Param("status") Integer status);

    /**
     * 根据关键词搜索模板
     *
     * @param keyword 关键词
     * @param status 状态（可选）
     * @return 模板列表
     */
    List<UmpMsgTemplate> searchTemplates(@Param("keyword") String keyword,
                                        @Param("status") Integer status);

    /**
     * 获取模板使用统计
     *
     * @param templateCode 模板代码（可选）
     * @return 使用统计
     */
    List<Map<String, Object>> selectTemplateUsageStatistics(@Param("templateCode") String templateCode);
}