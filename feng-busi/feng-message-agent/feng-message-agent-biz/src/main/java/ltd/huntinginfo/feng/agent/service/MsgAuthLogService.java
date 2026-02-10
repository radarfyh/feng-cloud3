package ltd.huntinginfo.feng.agent.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import ltd.huntinginfo.feng.agent.api.entity.MsgAuthLog;

import java.util.List;

public interface MsgAuthLogService {

    /**
     * 分页查询认证日志
     */
    IPage<MsgAuthLog> page(IPage page, MsgAuthLog dto);

    /**
     * 查询认证日志列表
     */
    List<MsgAuthLog> list(MsgAuthLog dto);

    /**
     * 记录认证日志
     */
    boolean save(MsgAuthLog dto);

    /**
     * 批量记录认证日志
     */
    boolean saveBatch(List<MsgAuthLog> dtos);
}