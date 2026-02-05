package work.metanet.feng.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.metanet.feng.admin.api.dto.RoleApplicationDTO;
import work.metanet.feng.admin.api.entity.SysRoleApplication;
import work.metanet.feng.admin.api.vo.SysApplicationVO;
import work.metanet.feng.common.core.util.R;

import java.util.List;

/**
 * 角色外部应用关联表(RoleApplication)表服务接口
 *
 * @author edison
 * @since 2022-08-09 10:36:06
 */
public interface SysRoleApplicationService extends IService<SysRoleApplication> {

    /**
     * 根据角色id配置外部应用限
     *
     * @param roleApplicationDTO:
     * @return R
     */
    boolean insert(RoleApplicationDTO roleApplicationDTO);

    /**
     * 获取当前登录用的所有应用列表(内部应用+外部应用)
     *
     * @return R
     */
    R<List<SysApplicationVO>> getApplicationList(String isCollect,String appMac);
}
