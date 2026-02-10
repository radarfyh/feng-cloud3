package ltd.huntinginfo.feng.agent.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.huntinginfo.feng.agent.api.entity.MsgAgentMapping;

import java.util.List;

/**
 * 消息映射表 服务接口
 */
public interface MsgAgentMappingService extends IService<MsgAgentMapping> {

    /**
     * 根据ID查询消息映射详情
     */
    MsgAgentMapping getById(String id);
    
    /**
     * 根据应用标识和业务ID查询消息映射
     * @param appKey 应用标识
     * @param bizId 业务ID
     * @return 消息映射实体
     */
    MsgAgentMapping getByAppKeyAndBizId(String appKey, String bizId);
    
    /**
     * 分页查询消息映射列表
     */
    IPage<MsgAgentMapping> page(IPage<MsgAgentMapping> page, MsgAgentMapping msgAgentMapping);

    /**
     * 查询消息映射列表
     */
    List<MsgAgentMapping> list(MsgAgentMapping msgAgentMapping);

    /**
     * 新增消息映射
     */
    boolean save(MsgAgentMapping msgAgentMapping);

    /**
     * 更新消息映射
     */
    boolean updateById(MsgAgentMapping msgAgentMapping);

    /**
     * 删除消息映射
     */
    boolean removeById(String id);

	/**
	 * 根据应用标识查询最近的消息（按接收时间倒序）
	 */
	List<MsgAgentMapping> getRecentMessages(String appKey, int limit);

	/**
	 * 查询应用未读消息
	 */
	List<MsgAgentMapping> getUnreadMessages(String appKey);

	/**
	 * 根据部级消息编码查询消息映射
	 */
	MsgAgentMapping getByXxbm(String xxbm);

	/**
	 * 统计应用消息数量
	 */
	long countByAppKey(String appKey);

	/**
	 * 统计应用未读消息数量
	 */
	long countUnreadByAppKey(String appKey);
}