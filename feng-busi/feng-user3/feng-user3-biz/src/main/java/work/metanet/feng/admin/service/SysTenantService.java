package work.metanet.feng.admin.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import work.metanet.feng.admin.api.entity.SysTenant;
import work.metanet.feng.admin.api.vo.SysTenantVO;

public interface SysTenantService extends IService<SysTenant> {

	IPage<SysTenantVO> pageEntity(Page<SysTenant> page, SysTenant entity);

	List<SysTenantVO> listEntity(SysTenant entity);

	SysTenantVO getTenantListByUser(Integer userId);

}
