package work.metanet.feng.admin.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.service.IService;
import work.metanet.feng.admin.api.entity.SysOrgan;
import work.metanet.feng.common.core.util.R;

import java.util.List;

/**
 * 机构表(SysOrgan)表服务接口
 */
public interface SysOrganService extends IService<SysOrgan> {

    /**
     * 获取机构树
     */
    List<Tree<Integer>> selectTree(Integer tenantId, String organCode);

    /**
     * 新增机构
     */
    R saveSysOrgan(SysOrgan sysOrgan);

    /**
     * 修改机构
     */
    R updateSysOrganById(SysOrgan sysOrgan);

    /**
     * 删除机构
     */
    R deleteSysOrgan(Integer id);

    /**
     * 获取当前用户的机构列表
     */
    List<SysOrgan> getOrganListByUser(Integer userId);
}