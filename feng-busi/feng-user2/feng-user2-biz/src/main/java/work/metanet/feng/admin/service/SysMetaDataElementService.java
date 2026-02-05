package work.metanet.feng.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import work.metanet.feng.admin.api.entity.SysMetaDataElement;

/**
 * 单字段描述服务接口
 */
public interface SysMetaDataElementService extends IService<SysMetaDataElement> {

	LambdaQueryWrapper<SysMetaDataElement> convertWrapper(SysMetaDataElement SysMetaDataElement);
}