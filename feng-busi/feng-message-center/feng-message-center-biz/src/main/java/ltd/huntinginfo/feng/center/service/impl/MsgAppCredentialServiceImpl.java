package ltd.huntinginfo.feng.center.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import ltd.huntinginfo.feng.center.api.dto.MsgAppCredentialDTO;
import ltd.huntinginfo.feng.center.api.entity.MsgAppCredential;
import ltd.huntinginfo.feng.center.api.vo.MsgAppCredentialVO;
import ltd.huntinginfo.feng.center.mapper.MsgAppCredentialMapper;
import ltd.huntinginfo.feng.center.service.MsgAppCredentialService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MsgAppCredentialServiceImpl 
    extends ServiceImpl<MsgAppCredentialMapper, MsgAppCredential> 
    implements MsgAppCredentialService {

    @Override
    public MsgAppCredentialVO getById(String id) {
        return convertToVo(super.getById(id));
    }

    @Override
    public MsgAppCredentialVO getByAppKey(String appKey) {
        MsgAppCredential entity = baseMapper.selectByAppKey(appKey);
        return convertToVo(entity);
    }
    
    @Override
    public String getAppSecret(String appKey) {
        MsgAppCredential entity = baseMapper.selectByAppKey(appKey);
        return entity.getAppSecret();
    }

    @Override
    public IPage<MsgAppCredentialVO> page(IPage page, MsgAppCredentialDTO dto) {
        LambdaQueryWrapper<MsgAppCredential> wrapper = buildQueryWrapper(dto);
        IPage<MsgAppCredential> ret = baseMapper.selectPage(page, wrapper);
        return ret.convert(this::convertToVo);
    }

    @Override
    public List<MsgAppCredentialVO> list(MsgAppCredentialDTO dto) {
        LambdaQueryWrapper<MsgAppCredential> wrapper = buildQueryWrapper(dto);
        return baseMapper.selectList(wrapper).stream().map(this::convertToVo).collect(Collectors.toList());
    }

    @Override
    public boolean save(MsgAppCredentialDTO dto) {
        return super.save(convertToEntity(dto));
    }

    @Override
    public boolean updateById(MsgAppCredentialDTO dto) {
        return super.updateById(convertToEntity(dto));
    }

    @Override
    public boolean removeById(String id) {
        return super.removeById(id);
    }

    @Override
    public boolean validateCredential(String appKey, String appSecret) {
        MsgAppCredential credential = baseMapper.selectByAppKey(appKey);
        if (credential == null || !"0".equals(credential.getDelFlag())) {
            return false;
        }
        // 实际项目中应使用加密比对
        return appSecret.equals(credential.getAppSecret());
    }

    private LambdaQueryWrapper<MsgAppCredential> buildQueryWrapper(MsgAppCredentialDTO dto) {
        LambdaQueryWrapper<MsgAppCredential> wrapper = new LambdaQueryWrapper<>();
        if (dto != null) {
            wrapper.eq(StringUtils.isNotBlank(dto.getAppKey()), MsgAppCredential::getAppKey, dto.getAppKey())
                   .eq(StringUtils.isNotBlank(dto.getAppType()), MsgAppCredential::getAppType, dto.getAppType())
                   .eq(dto.getStatus() != null, MsgAppCredential::getStatus, dto.getStatus())
                   .eq(MsgAppCredential::getDelFlag, "0");
        }
        return wrapper;
    }
    
    /**
     * 转换为VO对象
     */
    private MsgAppCredentialVO convertToVo(MsgAppCredential app) {
        if (app == null) {
            return null;
        }
        
        MsgAppCredentialVO vo = new MsgAppCredentialVO();
        BeanUtil.copyProperties(app, vo);
        return vo;
    }

    /**
     * 转换为实体对象
     */
    private MsgAppCredential convertToEntity(MsgAppCredentialDTO dto) {
    	MsgAppCredential mac = new MsgAppCredential();
    	BeanUtil.copyProperties(dto, mac);
    	return mac;
    }
}