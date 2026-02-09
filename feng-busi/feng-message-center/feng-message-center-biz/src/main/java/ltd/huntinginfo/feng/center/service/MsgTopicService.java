package ltd.huntinginfo.feng.center.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import ltd.huntinginfo.feng.center.api.entity.MsgTopic;

import java.util.List;

/**
 * 消息主题表 服务接口
 */
public interface MsgTopicService extends IService<MsgTopic> {

    /**
     * 根据ID查询主题详情
     */
    MsgTopic getById(String id);

    /**
     * 分页查询主题列表
     */
    IPage<MsgTopic> page(IPage<MsgTopic> page, MsgTopic msgTopic);

    /**
     * 查询主题列表
     */
    List<MsgTopic> list(MsgTopic msgTopic);

    /**
     * 新增主题
     */
    boolean save(MsgTopic msgTopic);

    /**
     * 更新主题
     */
    boolean updateById(MsgTopic msgTopic);

    /**
     * 删除主题
     */
    boolean removeById(String id);
}