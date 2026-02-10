package ltd.huntinginfo.feng.agent.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import ltd.huntinginfo.feng.agent.api.dto.AusAuthLogDTO;
import ltd.huntinginfo.feng.agent.api.entity.AusAuthLog;
import ltd.huntinginfo.feng.agent.api.vo.AusAuthLogVO;
import ltd.huntinginfo.feng.agent.mapper.AusAuthLogMapper;
import ltd.huntinginfo.feng.agent.service.AusAuthLogService;

@Slf4j
@Service
public class AusAuthLogServiceImpl 
		extends ServiceImpl<AusAuthLogMapper, AusAuthLog> 
		implements AusAuthLogService {
    
    @Override
    public AusAuthLogVO getById(Integer id) {
        return convertToVo(super.getById(id));
    }

    @Override
    public IPage<AusAuthLogVO> page(IPage<AusAuthLog> page, AusAuthLogDTO ausAuthLog) {
        QueryWrapper<AusAuthLog> wrapper = buildQueryWrapper(ausAuthLog);
        IPage<AusAuthLog> ret = baseMapper.selectPage(page, wrapper);
        return ret.convert(this::convertToVo);
    }

    @Override
    public List<AusAuthLogVO> list(AusAuthLogDTO ausAuthLog) {
        QueryWrapper<AusAuthLog> wrapper = buildQueryWrapper(ausAuthLog);
        List<AusAuthLog> list = baseMapper.selectList(wrapper);
        return list.stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());
    }

    @Override
    public boolean save(AusAuthLogDTO ausAuthLog) {
    	boolean ret = super.save(convertToEntity(ausAuthLog));
        return ret;
    }

    @Override
    public boolean saveBatch(List<AusAuthLogDTO> ausAuthLogs) {
        return super.saveBatch(ausAuthLogs.stream().map(this::convertToEntity).collect(Collectors.toList()));
    }

    @Override
    public boolean updateById(AusAuthLogDTO ausAuthLog) {
        return super.updateById(convertToEntity(ausAuthLog));
    }

    @Override
    public boolean removeById(Integer id) {
        return super.removeById(id);
    }

    private QueryWrapper<AusAuthLog> buildQueryWrapper(AusAuthLogDTO ausAuthLog) {
        QueryWrapper<AusAuthLog> wrapper = new QueryWrapper<>();
        
        if (ausAuthLog != null) {
            if (StringUtils.isNotBlank(ausAuthLog.getUserId())) {
                wrapper.eq("user_id", ausAuthLog.getUserId());
            }
            if (StringUtils.isNotBlank(ausAuthLog.getAusId())) {
                wrapper.eq("aus_id", ausAuthLog.getAusId());
            }
            if (StringUtils.isNotBlank(ausAuthLog.getProCode())) {
                wrapper.eq("pro_code", ausAuthLog.getProCode());
            }
            if (StringUtils.isNotBlank(ausAuthLog.getCityCode())) {
                wrapper.eq("city_code", ausAuthLog.getCityCode());
            }
            if (StringUtils.isNotBlank(ausAuthLog.getRegCode())) {
                wrapper.eq("reg_code", ausAuthLog.getRegCode());
            }
            if (StringUtils.isNotBlank(ausAuthLog.getUnitNo())) {
                wrapper.eq("unit_no", ausAuthLog.getUnitNo());
            }
            if (StringUtils.isNotBlank(ausAuthLog.getOrgCode())) {
                wrapper.eq("org_code", ausAuthLog.getOrgCode());
            }
            
            if (StringUtils.isNotBlank(ausAuthLog.getUsername())) {
                wrapper.eq("username", ausAuthLog.getUsername());
            }
            if (StringUtils.isNotBlank(ausAuthLog.getNickname())) {
                wrapper.eq("nickname", ausAuthLog.getNickname());
            }
            if (StringUtils.isNotBlank(ausAuthLog.getAccessToken())) {
                wrapper.eq("access_token", ausAuthLog.getAccessToken());
            }
        }
        
        wrapper.orderByDesc("create_time");
        return wrapper;
    }
    
    /**
     * 转换为VO对象，并填充关联数据
     */
    private AusAuthLogVO convertToVo(AusAuthLog ausAuthLog) {
    	AusAuthLogVO vo = new AusAuthLogVO();
        // 复制基础属性
    	BeanUtil.copyProperties(ausAuthLog, vo);
        
        return vo;
    }
    
    /**
     * 转换为实体对象
     */
    private AusAuthLog convertToEntity(AusAuthLogDTO dto) {
    	AusAuthLog aal = new AusAuthLog();
    	BeanUtil.copyProperties(dto, aal);
    	return aal;
    }
}
