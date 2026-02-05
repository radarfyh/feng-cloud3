package work.metanet.feng.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.metanet.feng.admin.api.entity.SysApplication;
import work.metanet.feng.admin.api.vo.SysApplicationVO;
import work.metanet.feng.admin.mapper.SysApplicationMapper;
import work.metanet.feng.admin.service.SysApplicationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 应用系统表(SysApplication)表服务实现类
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Service
@AllArgsConstructor
public class SysApplicationServiceImpl extends ServiceImpl<SysApplicationMapper, SysApplication> implements SysApplicationService {

    @Override
    public List<SysApplicationVO> getAppListByUserId(Integer userId) {
        return baseMapper.getAppListByUserId(userId);
    }

    @Override
    public List<SysApplicationVO> getAppListByRoleId(Integer roleId) {
        return baseMapper.getAppListByRoleId(roleId);
    }

    @Override
    public List<SysApplication> getApplicationListByUserId(Integer userId) {
        return baseMapper.getApplicationListByUserId(userId);
    }

    @Override
    public IPage getApplicationByUserPage(Page page, SysApplication sysApplication) {
        //管理端应用列表所有角色应该都能看到
        return this.page(page, Wrappers.<SysApplication>lambdaQuery().like(StringUtils.isNotBlank(sysApplication.getAppName()), SysApplication::getAppName, sysApplication.getAppName()).eq(StringUtils.isNotBlank(sysApplication.getApplicationCode()), SysApplication::getApplicationCode, sysApplication.getApplicationCode()).eq(StringUtils.isNotBlank(sysApplication.getStatus()), SysApplication::getStatus, sysApplication.getStatus()).eq(StringUtils.isNotBlank(sysApplication.getIsFengPortal()), SysApplication::getIsFengPortal, sysApplication.getIsFengPortal()).eq(StringUtils.isNotBlank(sysApplication.getFengType()), SysApplication::getFengType, sysApplication.getFengType()).eq(StringUtils.isNotBlank(sysApplication.getClientType()), SysApplication::getClientType, sysApplication.getClientType()).eq(StringUtils.isNotBlank(sysApplication.getIsMicro()), SysApplication::getIsMicro, sysApplication.getIsMicro()).eq(StringUtils.isNotBlank(sysApplication.getSysIsShow()), SysApplication::getSysIsShow, sysApplication.getSysIsShow()).orderByDesc(SysApplication::getCreateTime));
    }
}