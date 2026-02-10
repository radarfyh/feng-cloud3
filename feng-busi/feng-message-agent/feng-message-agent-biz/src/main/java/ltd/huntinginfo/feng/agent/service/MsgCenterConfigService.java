package ltd.huntinginfo.feng.agent.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.huntinginfo.feng.agent.api.entity.MsgCenterConfig;

import java.util.List;

/**
 * 部级配置表 服务接口
 */
public interface MsgCenterConfigService extends IService<MsgCenterConfig> {

    /**
     * 根据ID查询配置详情
     */
    MsgCenterConfig getById(String id);

    /**
     * 分页查询配置列表
     */
    IPage<MsgCenterConfig> page(IPage<MsgCenterConfig> page, MsgCenterConfig msgCenterConfig);

    /**
     * 查询配置列表
     */
    List<MsgCenterConfig> list(MsgCenterConfig msgCenterConfig);

    /**
     * 新增配置
     */
    boolean save(MsgCenterConfig msgCenterConfig);

    /**
     * 更新配置
     */
    boolean updateById(MsgCenterConfig msgCenterConfig);

    /**
     * 删除配置
     */
    boolean removeById(String id);

	/**
	 * 是否启用桩代码
	 */
	boolean isMockEnabled();

	/**
	 * 根据配置键获取配置值
	 */
	String getConfigValue(String configKey);
}