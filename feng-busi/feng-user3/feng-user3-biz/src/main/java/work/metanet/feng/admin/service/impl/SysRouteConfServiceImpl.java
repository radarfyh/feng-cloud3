package work.metanet.feng.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import work.metanet.feng.admin.mapper.SysRouteConfMapper;
import work.metanet.feng.admin.api.entity.SysRouteConf;
import work.metanet.feng.admin.service.SysRouteConfService;
import work.metanet.feng.admin.util.DynamicRouteUtil;
import work.metanet.feng.common.data.datascope.DataScope;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

/**
 * 路由配置表(SysRouteConf)表服务实现类
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Service
@AllArgsConstructor
public class SysRouteConfServiceImpl extends ServiceImpl<SysRouteConfMapper, SysRouteConf> 
    implements SysRouteConfService {
    
    /**
     * 获取带数据权限的路由列表
     */
	@Override
    public List<SysRouteConf> listByScope(Wrapper<SysRouteConf> queryWrapper) {
        // 获取当前用户的数据权限范围
        DataScope dataScope = DataScope.of();
        return baseMapper.selectListByScope(queryWrapper, dataScope);
    }
    
    /**
     * 获取有效的路由定义列表（过滤已删除和Swagger路由）
     */
	@Override
    public List<SysRouteConf> listActiveRoutes() {
        List<SysRouteConf> allRoutes = this.listByScope(
            new QueryWrapper<SysRouteConf>().eq("del_flag", "0"));
        
        return allRoutes.stream()
            .filter(route -> !DynamicRouteUtil.isSwaggerRoute(route))
            .collect(Collectors.toList());
    }
    
}