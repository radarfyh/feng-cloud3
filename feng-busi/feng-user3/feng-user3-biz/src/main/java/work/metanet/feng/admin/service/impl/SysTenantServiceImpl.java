package work.metanet.feng.admin.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import work.metanet.feng.admin.api.entity.SysTenant;
import work.metanet.feng.admin.api.entity.SysTenantConfig;
import work.metanet.feng.admin.api.entity.SysUser;
import work.metanet.feng.admin.api.vo.SysTenantVO;
import work.metanet.feng.admin.mapper.SysTenantConfigMapper;
import work.metanet.feng.admin.mapper.SysTenantMapper;
import work.metanet.feng.admin.mapper.SysUserMapper;
import work.metanet.feng.admin.service.SysTenantService;
import work.metanet.feng.common.core.constant.CommonConstants;

@Service
@AllArgsConstructor
public class SysTenantServiceImpl extends ServiceImpl<SysTenantMapper, SysTenant> implements SysTenantService {
	private final SysTenantConfigMapper sysTenantConfigMapper;
	private final SysUserMapper sysUserMapper;
	
    @Override
    public IPage<SysTenantVO> pageEntity(Page<SysTenant> page, SysTenant entity) {
        Page<SysTenant> entityPage = baseMapper.selectPage(page, getWrapper(entity));
        IPage<SysTenantVO> ret = entityPage.convert(this::convertToVO);
        return ret;
    }

    LambdaQueryWrapper<SysTenant> getWrapper(SysTenant entity) {
        // 构建查询条件
        LambdaQueryWrapper<SysTenant> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(entity.getId() != null && entity.getId() > 0, SysTenant::getId, entity.getId())
        	   .like(StrUtil.isNotBlank(entity.getTenantCode()), SysTenant::getTenantCode, entity.getTenantCode())
        	   .like(StrUtil.isNotBlank(entity.getTenantName()), SysTenant::getTenantName, entity.getTenantName())
        	   .like(StrUtil.isNotBlank(entity.getDescription()), SysTenant::getDescription, entity.getDescription())
        	   .eq(entity.getStatus() != null, SysTenant::getStatus, entity.getStatus())
        	   .le(entity.getEnableTime() != null, SysTenant::getEnableTime, entity.getEnableTime())
        	   .ge(entity.getExpireTime() != null, SysTenant::getExpireTime, entity.getExpireTime())
        	   .like(StrUtil.isNotBlank(entity.getContactEmail()), SysTenant::getContactEmail, entity.getContactEmail())
        	   .like(StrUtil.isNotBlank(entity.getContactName()), SysTenant::getContactName, entity.getContactName())
        	   .like(StrUtil.isNotBlank(entity.getContactPhone()), SysTenant::getContactPhone, entity.getContactPhone())
        	   .eq(entity.getBillingPlan() != null, SysTenant::getBillingPlan, entity.getBillingPlan())
        	   .eq(entity.getPlanName() != null, SysTenant::getPlanName, entity.getPlanName())
        	   .eq(entity.getPaymentStatus() != null, SysTenant::getPaymentStatus, entity.getPaymentStatus())
        	   .eq(entity.getIsolationMode() != null, SysTenant::getIsolationMode, entity.getIsolationMode())
               .orderByDesc(SysTenant::getId)
               .last(CommonConstants.LIMIT_LIST_QUERY);  // 限制最大数量
        return wrapper;
    }
    
    @Override
    public List<SysTenantVO> listEntity(SysTenant entity) {
        // 转换结果
        return baseMapper.selectList(getWrapper(entity)).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    private SysTenantVO convertToVO(SysTenant entity) {
    	SysTenantVO vo = new SysTenantVO();
        BeanUtil.copyProperties(entity, vo);
        
        LambdaQueryWrapper<SysTenantConfig> wrapper = Wrappers.lambdaQuery();
        SysTenantConfig config = sysTenantConfigMapper.selectOne(wrapper
        		.eq(entity.getId() != null && entity.getId() > 0, SysTenantConfig::getTenantId, entity.getId()));
        vo.setConfig(config);
        return vo;
    }

	@Override
	public SysTenantVO getTenantListByUser(Integer userId) {
		SysUser user = sysUserMapper.selectById(userId);
		SysTenant tenant = baseMapper.selectById(user.getTenantId());
		return convertToVO(tenant);
	}
}
