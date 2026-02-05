package work.metanet.feng.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.metanet.feng.admin.api.entity.SysAffiliation;
import work.metanet.feng.common.core.util.R;

import java.util.List;

/**
 * 联盟信息表(SysAffiliation)表服务接口
 *
 * @author edison
 * @since 2023-08-02 09:50:08
 */
public interface SysAffiliationService extends IService<SysAffiliation> {

    /**
     * 根据机构编码查询联盟列表
     *
     * @param organCode
     * @return
     */
    R<List<SysAffiliation>> getOrganCodeBySysAffiliationList(String organCode);
}
