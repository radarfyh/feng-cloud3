package ltd.huntinginfo.feng.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import ltd.huntinginfo.feng.center.api.entity.UmpMsgTopic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 消息主题表Mapper接口
 */
@Mapper
public interface UmpMsgTopicMapper extends BaseMapper<UmpMsgTopic> {

    /**
     * 根据主题代码查询主题
     *
     * @param topicCode 主题代码
     * @return 主题实体
     */
    UmpMsgTopic selectByTopicCode(@Param("topicCode") String topicCode);

    /**
     * 分页查询主题列表
     *
     * @param page 分页参数
     * @param topicName 主题名称（可选）
     * @param topicType 主题类型（可选）
     * @param status 状态（可选）
     * @param topicCode 主题代码（可选）
     * @return 分页结果
     */
    IPage<UmpMsgTopic> selectTopicPage(IPage<UmpMsgTopic> page,
                                      @Param("topicName") String topicName,
                                      @Param("topicType") String topicType,
                                      @Param("status") Integer status,
                                      @Param("topicCode") String topicCode);

    /**
     * 根据状态查询主题列表
     *
     * @param status 状态
     * @return 主题列表
     */
    List<UmpMsgTopic> selectByStatus(@Param("status") Integer status);

    /**
     * 查询可用的主题列表
     *
     * @return 可用的主题列表
     */
    List<UmpMsgTopic> selectAvailableTopics();

    /**
     * 更新主题的订阅者数量
     *
     * @param topicId 主题ID
     * @param subscriberCount 订阅者数量
     * @return 更新条数
     */
    int updateSubscriberCount(@Param("topicId") String topicId,
                             @Param("subscriberCount") Integer subscriberCount);

    /**
     * 批量更新主题状态
     *
     * @param ids 主题ID列表
     * @param status 目标状态
     * @return 更新条数
     */
    int batchUpdateStatus(@Param("ids") List<String> ids,
                         @Param("status") Integer status);

    /**
     * 获取主题统计信息
     *
     * @return 统计结果
     */
    Map<String, Object> selectTopicStatistics();
}