package work.metanet.feng.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.metanet.feng.admin.api.dto.RoleApplicationDTO;
import work.metanet.feng.admin.api.entity.SysRoleApplication;
import work.metanet.feng.admin.api.entity.SysApplication;
import work.metanet.feng.admin.api.vo.SysApplicationVO;
import work.metanet.feng.admin.mapper.SysRoleApplicationMapper;
import work.metanet.feng.admin.service.SysRoleApplicationService;
import work.metanet.feng.admin.service.SysApplicationService;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.security.util.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 角色外部应用关联表(RoleApplication)表服务实现类
 *
 * @author edison
 * @since 2022-08-09 10:36:06
 */
@Service
@AllArgsConstructor
public class SysRoleApplicationServiceImpl extends ServiceImpl<SysRoleApplicationMapper, SysRoleApplication> implements SysRoleApplicationService {

    private final SysApplicationService sysApplicationService;

    @Transactional
    @Override
    public boolean insert(RoleApplicationDTO roleApplicationDTO) {
        //先删除角色id对应的所有应用
        this.remove(Wrappers.<SysRoleApplication>lambdaQuery()
        		.eq(SysRoleApplication::getRoleId, roleApplicationDTO.getRoleId()));
        //再新增权限
        List<Integer> applicationIds = roleApplicationDTO.getApplicationIds();
        if (null != applicationIds && applicationIds.size() > 0) {
            baseMapper.saveRoleApplications(roleApplicationDTO.getRoleId(), roleApplicationDTO.getApplicationIds());

        } else {
            //删除当前角色对应的所有外部应用权限
            baseMapper.delete(Wrappers.<SysRoleApplication>lambdaQuery()
            		.eq(SysRoleApplication::getRoleId, roleApplicationDTO.getRoleId()));
        }
        return true;
    }

    @Override
    public R<List<SysApplicationVO>> getApplicationList(String isCollect, String appMac) {
        List<Integer> roles = SecurityUtils.getRoles();
        Integer userId = SecurityUtils.getUser().getId();
        String username = SecurityUtils.getUser().getUsername();
        String orgCode = SecurityUtils.getUser().getOrgCode();
        List<SysApplication> list;
        if (roles.contains(1)) {
            //超级管理员
            list = sysApplicationService.list(Wrappers.<SysApplication>lambdaQuery().eq(SysApplication::getIsFengPortal, "1"));
        } else {
            //获取内部应用
            list = sysApplicationService.getApplicationListByUserId(userId);
            //获取外部应用
            List<SysApplication> applicationList = baseMapper.getApplicationList(userId);
            list.addAll(applicationList);
        }
        List<SysApplicationVO> sysApplicationVOS = BeanUtil.copyToList(list, SysApplicationVO.class);
        return R.ok(sysApplicationVOS);
    }
}
