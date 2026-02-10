package ltd.huntinginfo.feng.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ltd.huntinginfo.feng.center.api.entity.UmpTopicSubscription;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 主题订阅表Mapper接口
 */
@Mapper
public interface UmpTopicSubscriptionMapper extends BaseMapper<UmpTopicSubscription> {

    /**
     * 根据主题代码和应用标识查询订阅
     *
     * @param topicCode 主题代码
     * @param appKey 应用标识
     * @return 订阅实体
     */
    UmpTopicSubscription selectByTopicAndApp(@Param("topicCode") String topicCode, 
                                            @Param("appKey") String appKey);

    /**
     * 分页查询订阅列表
     *
     * @param page 分页参数
     * @param topicCode 主题代码（可选）
     * @param appKey 应用标识（可选）
     * @param status 状态（可选）
     * @param pushMode 推送方式（可选）
     * @return 分页结果
     */
    IPage<UmpTopicSubscription> selectSubscriptionPage(Page<UmpTopicSubscription> page,
                                                      @Param("topicCode") String topicCode,
                                                      @Param("appKey") String appKey,
                                                      @Param("status") Integer status,
                                                      @Param("pushMode") String pushMode);

    /**
     * 根据主题代码查询订阅列表
     *
     * @param topicCode 主题代码
     * @param status 状态（可选）
     * @return 订阅列表
     */
    List<UmpTopicSubscription> selectByTopicCode(@Param("topicCode") String topicCode,
                                                @Param("status") Integer status);

    /**
     * 根据应用标识查询订阅列表
     *
     * @param appKey 应用标识
     * @param status 状态（可选）
     * @return 订阅列表
     */
    List<UmpTopicSubscription> selectByAppKey(@Param("appKey") String appKey,
                                             @Param("status") Integer status);

    /**
     * 更新订阅状态
     *
     * @param id 订阅ID
     * @param status 状态
     * @param unsubscribeTime 取消订阅时间
     * @return 更新条数
     */
    int updateSubscriptionStatus(@Param("id") String id,
                                @Param("status") Integer status,
                                @Param("unsubscribeTime") LocalDateTime unsubscribeTime);

    /**
     * 更新订阅统计信息
     *
     * @param id 订阅ID
     * @param messageCount 消息数量
     * @param lastMessageTime 最后消息时间
     * @return 更新条数
     */
    int updateSubscriptionStats(@Param("id") String id,
                               @Param("messageCount") Integer messageCount,
                               @Param("lastMessageTime") LocalDateTime lastMessageTime);

    /**
     * 批量更新订阅状态
     *
     * @param ids 订阅ID列表
     * @param status 目标状态
     * @param unsubscribeTime 取消订阅时间
     * @return 更新条数
     */
    int batchUpdateStatus(@Param("ids") List<String> ids,
                         @Param("status") Integer status,
                         @Param("unsubscribeTime") LocalDateTime unsubscribeTime);

    /**
     * 获取订阅统计信息
     *
     * @param topicCode 主题代码（可选）
     * @param appKey 应用标识（可选）
     * @return 统计结果
     */
    Map<String, Object> selectSubscriptionStatistics(@Param("topicCode") String topicCode,
                                                    @Param("appKey") String appKey);

    /**
     * 检查订阅是否存在
     *
     * @param topicCode 主题代码
     * @param appKey 应用标识
     * @param status 状态（可选）
     * @return 是否存在
     */
    boolean existsSubscription(@Param("topicCode") String topicCode,
                              @Param("appKey") String appKey,
                              @Param("status") Integer status);

    /**
     * 获取活跃订阅数量
     *
     * @param topicCode 主题代码
     * @return 活跃订阅数量
     */
    Integer countActiveSubscriptions(@Param("topicCode") String topicCode);
}