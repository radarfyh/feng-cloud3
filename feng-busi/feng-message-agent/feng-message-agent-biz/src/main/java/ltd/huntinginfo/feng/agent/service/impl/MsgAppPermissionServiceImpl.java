package ltd.huntinginfo.feng.agent.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import ltd.huntinginfo.feng.agent.api.dto.MsgAppPermissionDTO;
import ltd.huntinginfo.feng.agent.api.entity.MsgAppPermission;
import ltd.huntinginfo.feng.agent.api.vo.MsgAppPermissionVO;
import ltd.huntinginfo.feng.agent.mapper.MsgAppPermissionMapper;
import ltd.huntinginfo.feng.agent.service.MsgAppPermissionService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MsgAppPermissionServiceImpl 
    extends ServiceImpl<MsgAppPermissionMapper, MsgAppPermission> 
    implements MsgAppPermissionService {

    @Override
    public MsgAppPermissionVO getById(String id) {
        return convertToVo(super.getById(id));
    }

    @Override
    public IPage<MsgAppPermissionVO> page(IPage page, MsgAppPermissionDTO dto) {
        LambdaQueryWrapper<MsgAppPermission> wrapper = buildQueryWrapper(dto);
        IPage<MsgAppPermission> ret = baseMapper.selectPage(page, wrapper);
        return ret.convert(this::convertToVo);
    }
    
    @Override
    public List<MsgAppPermissionVO> list(MsgAppPermissionDTO dto) {
        LambdaQueryWrapper<MsgAppPermission> wrapper = buildQueryWrapper(dto);
        List<MsgAppPermission> ret = baseMapper.selectList(wrapper);
        return ret.stream().map(this::convertToVo).collect(Collectors.toList());
    }

    @Override
    public List<MsgAppPermissionVO> listActivePermissions(String appKey) {
        return baseMapper.selectActivePermissions(appKey).stream().map(this::convertToVo).collect(Collectors.toList());
    }

    @Override
    public boolean save(MsgAppPermissionDTO dto) {
        return super.save(convertToEntity(dto));
    }

    @Override
    public boolean saveBatch(List<MsgAppPermissionDTO> dtos) {
        List<MsgAppPermission> entities = dtos.stream().map(this::convertToEntity).collect(Collectors.toList());
        return super.saveBatch(entities);
    }

    @Override
    public boolean updateById(MsgAppPermissionDTO dto) {
        return super.updateById(convertToEntity(dto));
    }

    @Override
    public boolean removeById(String id) {
        return super.removeById(id);
    }

    @Override
    public boolean checkPermissionExists(String appKey, String resourceCode) {
        LambdaQueryWrapper<MsgAppPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MsgAppPermission::getAppKey, appKey)
               .eq(MsgAppPermission::getResourceCode, resourceCode)
               .eq(MsgAppPermission::getStatus, 1);
        return baseMapper.selectCount(wrapper) > 0;
    }

    private LambdaQueryWrapper<MsgAppPermission> buildQueryWrapper(MsgAppPermissionDTO dto) {
        LambdaQueryWrapper<MsgAppPermission> wrapper = new LambdaQueryWrapper<>();
        if (dto != null) {
            wrapper.eq(StringUtils.isNotBlank(dto.getAppKey()), MsgAppPermission::getAppKey, dto.getAppKey())
                   .eq(StringUtils.isNotBlank(dto.getResourceCode()), MsgAppPermission::getResourceCode, dto.getResourceCode())
                   .eq(dto.getStatus() != null, MsgAppPermission::getStatus, dto.getStatus())
                   .eq(MsgAppPermission::getDelFlag, "0");
        }
        return wrapper;
    }

    /**
     * 转换为VO对象
     */
    private MsgAppPermissionVO convertToVo(MsgAppPermission app) {
        if (app == null) {
            return null;
        }
        
        MsgAppPermissionVO vo = new MsgAppPermissionVO();
        BeanUtil.copyProperties(app, vo);
        return vo;
    }

    /**
     * 转换为实体对象
     */
    private MsgAppPermission convertToEntity(MsgAppPermissionDTO dto) {
    	MsgAppPermission mac = new MsgAppPermission();
    	BeanUtil.copyProperties(dto, mac);
    	return mac;
    }
}
