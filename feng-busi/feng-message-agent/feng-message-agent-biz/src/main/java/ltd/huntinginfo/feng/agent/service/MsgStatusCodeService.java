package ltd.huntinginfo.feng.agent.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.huntinginfo.feng.agent.api.entity.MsgStatusCode;

import java.util.List;

/**
 * 消息状态码表 服务接口
 */
public interface MsgStatusCodeService extends IService<MsgStatusCode> {

    /**
     * 根据ID查询状态码详情
     */
    MsgStatusCode getById(String id);

    /**
     * 分页查询状态码列表
     */
    IPage<MsgStatusCode> page(IPage<MsgStatusCode> page, MsgStatusCode msgStatusCode);

    /**
     * 查询状态码列表
     */
    List<MsgStatusCode> list(MsgStatusCode msgStatusCode);

    /**
     * 新增状态码
     */
    boolean save(MsgStatusCode msgStatusCode);

    /**
     * 更新状态码
     */
    boolean updateById(MsgStatusCode msgStatusCode);

    /**
     * 删除状态码
     */
    boolean removeById(String id);
}