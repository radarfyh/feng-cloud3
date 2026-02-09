package ltd.huntinginfo.feng.admin.service.impl.dict;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import ltd.huntinginfo.feng.admin.api.dto.dict.UniqueOrgInfoDTO;
import ltd.huntinginfo.feng.admin.api.entity.dict.UniqueOrg;
import ltd.huntinginfo.feng.admin.api.vo.dict.UniqueOrgInfoVO;
import ltd.huntinginfo.feng.admin.mapper.dict.UniqueOrgMapper;
import ltd.huntinginfo.feng.admin.service.dict.UniqueOrgService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UniqueOrgServiceImpl 
    extends ServiceImpl<UniqueOrgMapper, UniqueOrg> 
    implements UniqueOrgService {

    @Override
    public UniqueOrgInfoVO getById(Integer id) {
        return convertToVo(super.getById(id));
    }

    @Override
    public IPage<UniqueOrgInfoVO> page(IPage page, UniqueOrgInfoDTO uniqueOrgInfo) {
        QueryWrapper<UniqueOrg> wrapper = buildQueryWrapper(uniqueOrgInfo);
        IPage<UniqueOrg> ret = baseMapper.selectPage(page, wrapper);
        return ret.convert(this::convertToVo);
    }

    @Override
    public List<UniqueOrgInfoVO> list(UniqueOrgInfoDTO uniqueOrgInfo) {
        QueryWrapper<UniqueOrg> wrapper = buildQueryWrapper(uniqueOrgInfo);
        List<UniqueOrg> list = baseMapper.selectList(wrapper);
        return list.stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());
    }

    @Override
    public boolean save(UniqueOrgInfoDTO uniqueOrgInfo) {
    	
        return super.save(convertToEntity(uniqueOrgInfo));
    }
    
    @Override
    public boolean saveBatch(List<UniqueOrgInfoDTO> uniqueOrgInfos) {
        return super.saveBatch(uniqueOrgInfos.stream().map(this::convertToEntity).collect(Collectors.toList()));
    }

    @Override
    public boolean updateById(UniqueOrgInfoDTO uniqueOrgInfo) {
        return super.updateById(convertToEntity(uniqueOrgInfo));
    }

    @Override
    public boolean removeById(Integer id) {
        return super.removeById(id);
    }

    private QueryWrapper<UniqueOrg> buildQueryWrapper(UniqueOrgInfoDTO uniqueOrgInfo) {
        QueryWrapper<UniqueOrg> wrapper = new QueryWrapper<>();
        
        if (uniqueOrgInfo != null) {
            // 机构名称模糊查询
            if (StringUtils.isNotBlank(uniqueOrgInfo.getOrgName())) {
                wrapper.like("org_name", uniqueOrgInfo.getOrgName());
            }
            
            // 机构代码精确查询
            if (StringUtils.isNotBlank(uniqueOrgInfo.getOrgCode())) {
                wrapper.eq("org_code", uniqueOrgInfo.getOrgCode());
            }
            
            // 上级机构查询
            if (StringUtils.isNotBlank(uniqueOrgInfo.getParentId())) {
                wrapper.eq("parent_id", uniqueOrgInfo.getParentId());
            }
            
            // 省份查询
            if (StringUtils.isNotBlank(uniqueOrgInfo.getProId())) {
                wrapper.eq("pro_id", uniqueOrgInfo.getProId());
            }
            
            // 城市查询
            if (StringUtils.isNotBlank(uniqueOrgInfo.getCityId())) {
                wrapper.eq("city_id", uniqueOrgInfo.getCityId());
            }
            
            // 区域查询
            if (StringUtils.isNotBlank(uniqueOrgInfo.getRegId())) {
                wrapper.eq("reg_id", uniqueOrgInfo.getRegId());
            }
        }
        
        wrapper.orderByAsc("order_id").orderByDesc("create_time");
        return wrapper;
    }
    
    /**
     * 转换为VO对象
     */
    private UniqueOrgInfoVO convertToVo(UniqueOrg info) {
        if (info == null) {
            return null;
        }
        
        UniqueOrgInfoVO vo = new UniqueOrgInfoVO();
        BeanUtil.copyProperties(info, vo);
        return vo;
    }

    /**
     * 转换为实体对象
     */
    private UniqueOrg convertToEntity(UniqueOrgInfoDTO uniqueOrgInfo) {
    	UniqueOrg doi = new UniqueOrg();
    	BeanUtil.copyProperties(uniqueOrgInfo, doi);
    	return doi;
    }
    
	@Override
    @Cacheable(key = "UniqueOrgInfo_" + "#code")
	public UniqueOrg getByCode(String code) {
		LambdaQueryWrapper<UniqueOrg> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(UniqueOrg::getOrgCode, code);
		UniqueOrg ret = baseMapper.selectOne(wrapper);
		return ret;
	}

	@Override
    @Cacheable(key = "UniqueOrgInfo_" + "all")
	public List<UniqueOrg> getAllValidItems() {
		LambdaQueryWrapper<UniqueOrg> wrapper = Wrappers.lambdaQuery();
        wrapper.orderByAsc(UniqueOrg::getOrgCode);
        List<UniqueOrg> list = baseMapper.selectList(wrapper);
		return list;
	}

	@Override
    @CacheEvict(allEntries = true)
	public void refreshCache() {
		log.info("刷新统一机构信息缓存");
		
	}
}
