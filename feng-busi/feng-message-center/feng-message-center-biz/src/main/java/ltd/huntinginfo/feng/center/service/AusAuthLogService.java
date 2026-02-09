package ltd.huntinginfo.feng.center.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import ltd.huntinginfo.feng.center.api.dto.AusAuthLogDTO;
import ltd.huntinginfo.feng.center.api.entity.AusAuthLog;
import ltd.huntinginfo.feng.center.api.entity.AusAuthLog;
import ltd.huntinginfo.feng.center.api.vo.AusAuthLogVO;

public interface AusAuthLogService extends IService<AusAuthLog> {
    /**
     * 根据ID查询AUS认证日志详情
     */
	AusAuthLogVO getById(Integer id);

    /**
     * 分页查询AUS认证日志列表
     */
    IPage<AusAuthLogVO> page(IPage<AusAuthLog> page, AusAuthLogDTO ausAuthLog);

    /**
     * 查询AUS认证日志列表
     */
    List<AusAuthLogVO> list(AusAuthLogDTO ausAuthLog);

    /**
     * 新增AUS认证日志信息
     */
    boolean save(AusAuthLogDTO ausAuthLog);

    /**
     * 批量新增AUS认证日志信息
     */
    boolean saveBatch(List<AusAuthLogDTO> ausAuthLogs);

    /**
     * 更新AUS认证日志信息
     */
    boolean updateById(AusAuthLogDTO ausAuthLog);

    /**
     * 删除AUS认证日志信息
     */
    boolean removeById(Integer id);
}
