package work.metanet.feng.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import work.metanet.feng.admin.mapper.SysConfigMapper;
import work.metanet.feng.admin.api.entity.SysConfig;
import work.metanet.feng.admin.service.SysConfigService;
import org.springframework.stereotype.Service;

/**
 * 系统配置表(SysConfig)表服务实现类
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Service
@AllArgsConstructor
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

}