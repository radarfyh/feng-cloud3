package ltd.huntinginfo.feng.agent.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.huntinginfo.feng.agent.api.entity.MsgReceiverMapping;

import java.util.List;

/**
 * 接收者映射表 服务接口
 */
public interface MsgReceiverMappingService extends IService<MsgReceiverMapping> {

    /**
     * 根据ID查询接收者映射详情
     */
    MsgReceiverMapping getById(String id);

    /**
     * 分页查询接收者映射列表
     */
    IPage<MsgReceiverMapping> page(IPage<MsgReceiverMapping> page, MsgReceiverMapping msgReceiverMapping);

    /**
     * 查询接收者映射列表
     */
    List<MsgReceiverMapping> list(MsgReceiverMapping msgReceiverMapping);

    /**
     * 新增接收者映射
     */
    boolean save(MsgReceiverMapping msgReceiverMapping);

    /**
     * 更新接收者映射
     */
    boolean updateById(MsgReceiverMapping msgReceiverMapping);

    /**
     * 删除接收者映射
     */
    boolean removeById(String id);

	/**
	 * 根据部级个人接收者证件号码获取映射
	 */
	MsgReceiverMapping getByCenterPerson(String jsrzjhm);

	/**
	 * 根据部级单位接收者代码获取映射
	 */
	MsgReceiverMapping getByCenterUnit(String jsdwdm);
}