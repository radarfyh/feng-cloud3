package work.metanet.feng.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import work.metanet.feng.admin.api.entity.SysMetaDataElement;
import work.metanet.feng.admin.mapper.SysMetaDataElementMapper;
import work.metanet.feng.admin.service.SysMetaDataElementService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 单字段描述服务实现类
 */
@Service
@AllArgsConstructor
public class SysMetaDataElementServiceImpl extends ServiceImpl<SysMetaDataElementMapper, SysMetaDataElement> implements SysMetaDataElementService {


    @Override
    public LambdaQueryWrapper<SysMetaDataElement> convertWrapper(SysMetaDataElement metaDataElement){
    	LambdaQueryWrapper<SysMetaDataElement> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(metaDataElement.getId() != null && metaDataElement.getId() > 0,SysMetaDataElement::getId,metaDataElement.getId());
        wrapper.like(StringUtils.isNotEmpty(metaDataElement.getCategory()),SysMetaDataElement::getCategory,metaDataElement.getCategory());
        wrapper.like(StringUtils.isNotEmpty(metaDataElement.getIdentifier()),SysMetaDataElement::getIdentifier,metaDataElement.getIdentifier());
        wrapper.like(StringUtils.isNotEmpty(metaDataElement.getName()),SysMetaDataElement::getName,metaDataElement.getName());
        wrapper.like(StringUtils.isNotEmpty(metaDataElement.getDefine()),SysMetaDataElement::getDefine,metaDataElement.getDefine());
        wrapper.like(StringUtils.isNotEmpty(metaDataElement.getDataType()),SysMetaDataElement::getDataType,metaDataElement.getDataType());
        wrapper.like(StringUtils.isNotEmpty(metaDataElement.getRepresentFormat()),SysMetaDataElement::getRepresentFormat,metaDataElement.getRepresentFormat());
        wrapper.like(StringUtils.isNotEmpty(metaDataElement.getAllowableValue()),SysMetaDataElement::getAllowableValue,metaDataElement.getAllowableValue());
        wrapper.like(StringUtils.isNotEmpty(metaDataElement.getVersionNo()),SysMetaDataElement::getVersionNo,metaDataElement.getVersionNo());

        wrapper.eq(StringUtils.isNotEmpty(metaDataElement.getCategoryId()),SysMetaDataElement::getCategoryId,metaDataElement.getCategoryId());
        wrapper.eq(StringUtils.isNotEmpty(metaDataElement.getDataLength()),SysMetaDataElement::getDataLength,metaDataElement.getDataLength());
        wrapper.eq(StringUtils.isNotEmpty(metaDataElement.getUnit()),SysMetaDataElement::getUnit,metaDataElement.getUnit());
        wrapper.eq(StringUtils.isNotEmpty(metaDataElement.getStatus()),SysMetaDataElement::getStatus,metaDataElement.getStatus());
        wrapper.eq(StringUtils.isNotEmpty(metaDataElement.getUncriterion()),SysMetaDataElement::getUncriterion,metaDataElement.getUncriterion());
        wrapper.eq(StringUtils.isNotEmpty(metaDataElement.getDataCodeIn()),SysMetaDataElement::getDataCodeIn,metaDataElement.getDataCodeIn());

        if (StringUtils.isNotEmpty(metaDataElement.getSortBy())){
            wrapper.orderByAsc(SysMetaDataElement::getSortBy);
        }else {
            wrapper.orderByDesc(SysMetaDataElement::getCreateTime);
        }
        return wrapper;
    }
}