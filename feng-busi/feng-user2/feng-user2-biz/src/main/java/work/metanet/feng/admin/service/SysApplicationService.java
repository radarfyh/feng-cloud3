package work.metanet.feng.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import work.metanet.feng.admin.api.entity.SysApplication;
import work.metanet.feng.admin.api.vo.SysApplicationVO;

import java.util.List;

/**
 * 应用系统表(SysApplication)表服务接口
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
public interface SysApplicationService extends IService<SysApplication> {

    /**
     * 通过用户id获取应用列表
     *
     * @param userId:用户id
     * @return: java.util.List<work.metanet.feng.admin.api.entity.SysApplication>
     **/
    List<SysApplicationVO> getAppListByUserId(Integer userId);

    /**
     * 通过角色id获取应用列表
     *
     * @param roleId: 角色id
     * @return: java.util.List<work.metanet.feng.admin.api.vo.SysApplicationVO>
     **/
    List<SysApplicationVO> getAppListByRoleId(Integer roleId);

    /**
     * 根据角色id集合获取应用列表
     *
     * @param userId:
     * @return R
     */
    List<SysApplication> getApplicationListByUserId(Integer userId);

    /**
     * 分页查询当前登录登录用户的应用列表
     *
     * @param page
     * @param sysApplication
     * @return
     */
    IPage getApplicationByUserPage(Page page, SysApplication sysApplication);
}