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

import ltd.huntinginfo.feng.admin.api.dto.dict.UniqueUserInfoDTO;
import ltd.huntinginfo.feng.admin.api.entity.dict.UniqueUser;
import ltd.huntinginfo.feng.admin.api.vo.dict.UniqueUserInfoVO;
import ltd.huntinginfo.feng.admin.mapper.dict.UniqueUserMapper;
import ltd.huntinginfo.feng.admin.service.dict.UniqueUserService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UniqueUserInfoServiceImpl 
    extends ServiceImpl<UniqueUserMapper, UniqueUser> 
    implements UniqueUserService {

    @Override
    public UniqueUserInfoVO getById(Integer id) {
        return convertToVo(super.getById(id));
    }

    @Override
    public IPage<UniqueUserInfoVO> page(IPage page, UniqueUserInfoDTO uniqueUserInfo) {
        QueryWrapper<UniqueUser> wrapper = buildQueryWrapper(uniqueUserInfo);
        IPage<UniqueUser> ret = baseMapper.selectPage(page, wrapper);
        return ret.convert(this::convertToVo);
    }

    @Override
    public List<UniqueUserInfoVO> list(UniqueUserInfoDTO uniqueUserInfo) {
        QueryWrapper<UniqueUser> wrapper = buildQueryWrapper(uniqueUserInfo);
        List<UniqueUser> list = baseMapper.selectList(wrapper);
        return list.stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());
    }

    @Override
    public boolean save(UniqueUserInfoDTO uniqueUserInfo) {
        return super.save(convertToEntity(uniqueUserInfo));
    }

    @Override
    public boolean saveBatch(List<UniqueUserInfoDTO> uniqueUserInfos) {
        return super.saveBatch(uniqueUserInfos.stream().map(this::convertToEntity).collect(Collectors.toList()));
    }

    @Override
    public boolean updateById(UniqueUserInfoDTO uniqueUserInfo) {
        return super.updateById(convertToEntity(uniqueUserInfo));
    }

    @Override
    public boolean removeById(Integer id) {
        return super.removeById(id);
    }

    private QueryWrapper<UniqueUser> buildQueryWrapper(UniqueUserInfoDTO uniqueUserInfo) {
        QueryWrapper<UniqueUser> wrapper = new QueryWrapper<>();
        
        if (uniqueUserInfo != null) {
            // 用户名模糊查询
            if (StringUtils.isNotBlank(uniqueUserInfo.getUsername())) {
                wrapper.like("username", uniqueUserInfo.getUsername());
            }
            
            // 用户昵称模糊查询
            if (StringUtils.isNotBlank(uniqueUserInfo.getNickname())) {
                wrapper.like("nickname", uniqueUserInfo.getNickname());
            }
            
            // 用户类型查询
            if (StringUtils.isNotBlank(uniqueUserInfo.getType())) {
                wrapper.eq("type", uniqueUserInfo.getType());
            }
            
            // 身份证号精确查询
            if (StringUtils.isNotBlank(uniqueUserInfo.getIdCard())) {
                wrapper.eq("id_card", uniqueUserInfo.getIdCard());
            }
            
            // 机构查询
            if (StringUtils.isNotBlank(uniqueUserInfo.getOrgId())) {
                wrapper.eq("org_id", uniqueUserInfo.getOrgId());
            }
            
            // 手机号查询
            if (StringUtils.isNotBlank(uniqueUserInfo.getMobile())) {
                wrapper.eq("mobile", uniqueUserInfo.getMobile());
            }
        }
        
        wrapper.orderByDesc("create_time");
        return wrapper;
    }
    
    /**
     * 转换为VO对象
     */
    private UniqueUserInfoVO convertToVo(UniqueUser info) {
        if (info == null) {
            return null;
        }
        
        UniqueUserInfoVO vo = new UniqueUserInfoVO();
        BeanUtil.copyProperties(info, vo);
        return vo;
    }
    
    /**
     * 转换为实体对象
     */
    private UniqueUser convertToEntity(UniqueUserInfoDTO uniqueUserInfo) {
    	UniqueUser doi = new UniqueUser();
    	BeanUtil.copyProperties(uniqueUserInfo, doi);
    	return doi;
    }
    
	@Override
    @Cacheable(key = "UniqueUserInfo_" + "#code")
	public UniqueUser getByCode(String username) {
		LambdaQueryWrapper<UniqueUser> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(UniqueUser::getLoginId, username);
		UniqueUser ret = baseMapper.selectOne(wrapper);
		return ret;
	}

	@Override
    @Cacheable(key = "UniqueUserInfo_" + "all")
	public List<UniqueUser> getAllValidItems() {
		LambdaQueryWrapper<UniqueUser> wrapper = Wrappers.lambdaQuery();
        wrapper.orderByAsc(UniqueUser::getLoginId);
        List<UniqueUser> list = baseMapper.selectList(wrapper);
		return list;
	}

	@Override
    @CacheEvict(allEntries = true)
	public void refreshCache() {
		log.info("刷新统一用户信息缓存");
		
	}
}
