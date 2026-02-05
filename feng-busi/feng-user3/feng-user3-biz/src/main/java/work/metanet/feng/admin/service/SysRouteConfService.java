package work.metanet.feng.admin.service;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import work.metanet.feng.admin.api.entity.SysRouteConf;

/**
 * 路由配置表(SysRouteConf)表服务接口
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
public interface SysRouteConfService extends IService<SysRouteConf> {
	List<SysRouteConf> listByScope(Wrapper<SysRouteConf> queryWrapper);
	List<SysRouteConf> listActiveRoutes();
}