package work.metanet.feng.admin.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.AllArgsConstructor;
import work.metanet.feng.admin.api.entity.SysTenantConfig;
import work.metanet.feng.admin.mapper.SysTenantConfigMapper;
import work.metanet.feng.admin.service.SysTenantConfigService;

@Service
@AllArgsConstructor
public class SysTenantConfigServiceImpl  extends ServiceImpl<SysTenantConfigMapper, SysTenantConfig> implements SysTenantConfigService {

}
