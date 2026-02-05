package work.metanet.feng.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import work.metanet.feng.admin.mapper.SysLogMapper;
import work.metanet.feng.admin.api.entity.SysLog;
import work.metanet.feng.admin.service.SysLogService;
import org.springframework.stereotype.Service;

/**
 * 日志表(SysLog)表服务实现类
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Service
@AllArgsConstructor
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements SysLogService {

}