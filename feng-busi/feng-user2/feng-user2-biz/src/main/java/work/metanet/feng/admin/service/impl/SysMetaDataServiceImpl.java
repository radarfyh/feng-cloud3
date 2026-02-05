package work.metanet.feng.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import lombok.AllArgsConstructor;
import work.metanet.feng.admin.api.entity.SysMetaData;
import work.metanet.feng.admin.api.entity.SysMetaDataElement;
import work.metanet.feng.admin.api.entity.SysMetaDataElementRelation;
import work.metanet.feng.admin.api.vo.SysMetaDataVO;
import work.metanet.feng.admin.mapper.SysMetaDataElementMapper;
import work.metanet.feng.admin.mapper.SysMetaDataElementRelationMapper;
import work.metanet.feng.admin.mapper.SysMetaDataMapper;
import work.metanet.feng.admin.service.SysMetaDataService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 多字段描述服务实现类
 */
@Service
@AllArgsConstructor
public class SysMetaDataServiceImpl extends ServiceImpl<SysMetaDataMapper, SysMetaData> implements SysMetaDataService {
	private final SysMetaDataElementMapper sysMetaDataElementMapper;
	private final SysMetaDataElementRelationMapper sysMetaDataElementRelationMapper;

    @Override
    public LambdaQueryWrapper<SysMetaData> convertWrapper(SysMetaData sysMetaData){
    	LambdaQueryWrapper<SysMetaData> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(sysMetaData.getId() != null && sysMetaData.getId() > 0, SysMetaData::getId, sysMetaData.getId());
        wrapper.eq(sysMetaData.getParentId() != null && sysMetaData.getParentId() > 0, SysMetaData::getParentId, sysMetaData.getParentId());
        wrapper.like(StringUtils.isNotEmpty(sysMetaData.getCategory()),SysMetaData::getCategory,sysMetaData.getCategory());
        wrapper.like(StringUtils.isNotEmpty(sysMetaData.getIdentifier()),SysMetaData::getIdentifier,sysMetaData.getIdentifier());
        wrapper.like(StringUtils.isNotEmpty(sysMetaData.getName()),SysMetaData::getName,sysMetaData.getName());
        wrapper.orderByDesc(SysMetaData::getId);

        return wrapper;
    }
    
    @Override
    public IPage<SysMetaDataVO> pageEntity(Page<SysMetaData> page, SysMetaData entity) {
        Page<SysMetaData> entityPage = baseMapper.selectPage(page, convertWrapper(entity));
        IPage<SysMetaDataVO> ret = entityPage.convert(this::convertToVO);
        return ret;
    }
    
    @Override
    public List<SysMetaDataVO> listEntity(SysMetaData entity) {
        // 转换结果
        return baseMapper.selectList(convertWrapper(entity)).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    private SysMetaDataVO convertToVO(SysMetaData entity) {
    	SysMetaDataVO vo = new SysMetaDataVO();
        BeanUtil.copyProperties(entity, vo);
        
        LambdaQueryWrapper<SysMetaDataElementRelation> wrapperRelation = Wrappers.lambdaQuery();
        wrapperRelation.eq(entity.getId() != null && entity.getId() > 0, SysMetaDataElementRelation::getMetaDataId, entity.getId());
        List<SysMetaDataElementRelation> relations = sysMetaDataElementRelationMapper.selectList(wrapperRelation);
        
        List<SysMetaDataElement> elements = new ArrayList<SysMetaDataElement>();
        relations.forEach(relation -> {
            LambdaQueryWrapper<SysMetaDataElement> wrapperElement = Wrappers.lambdaQuery();
            wrapperElement.eq(entity.getId() != null && entity.getId() > 0, SysMetaDataElement::getId, entity.getId());
            elements.addAll(sysMetaDataElementMapper.selectList(wrapperElement));
        });

        vo.setElements(elements);
        return vo;
    }
}