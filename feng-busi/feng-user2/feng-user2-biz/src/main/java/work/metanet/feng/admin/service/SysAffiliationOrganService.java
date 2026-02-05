package work.metanet.feng.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.metanet.feng.admin.api.dto.SysAffiliationOrganDTO;
import work.metanet.feng.admin.api.entity.SysAffiliationOrgan;
import work.metanet.feng.admin.api.entity.SysOrgan;
import work.metanet.feng.common.core.util.R;

import java.util.List;

/**
 * 联盟机构关联表(SysAffiliationOrgan)表服务接口
 *
 * @author edison
 * @since 2023-08-02 09:53:02
 */
public interface SysAffiliationOrganService extends IService<SysAffiliationOrgan> {

    /**
     * 根据联盟id查询机构列表
     *
     * @param affiliationId
     * @return
     */
    List<SysOrgan> getOrganListByAffiliationId(Integer affiliationId);

    /**
     * 配置机构关联联盟
     *
     * @param sysAffiliationOrganDTO
     * @return
     */
    R configSysAffiliationOrgan(SysAffiliationOrganDTO sysAffiliationOrganDTO);

}
