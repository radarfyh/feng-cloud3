package work.metanet.feng.admin.service;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import work.metanet.feng.admin.api.entity.SysMetaData;
import work.metanet.feng.admin.api.vo.SysMetaDataVO;

/**
 * 多字段描述集合服务接口
 */
public interface SysMetaDataService extends IService<SysMetaData> {

	LambdaQueryWrapper<SysMetaData> convertWrapper(SysMetaData SysMetaDataSet);

	IPage<SysMetaDataVO> pageEntity(Page<SysMetaData> page, SysMetaData entity);

	List<SysMetaDataVO> listEntity(SysMetaData entity);
}