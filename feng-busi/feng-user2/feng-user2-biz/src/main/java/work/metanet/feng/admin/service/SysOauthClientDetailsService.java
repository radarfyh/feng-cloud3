package work.metanet.feng.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import work.metanet.feng.admin.api.dto.SysOauthClientDetailsDTO;
import work.metanet.feng.admin.api.entity.SysOauthClientDetails;
import work.metanet.feng.common.core.util.R;

/**
 * 终端信息表(SysOauthClientDetails)表服务接口
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
public interface SysOauthClientDetailsService extends IService<SysOauthClientDetails> {

    /**
     * 通过ID删除客户端
     * @param clientId
     * @return
     */
    Boolean removeByClientId(String clientId);

    /**
     * 根据客户端信息
     * @param clientDetailsDTO
     * @return
     */
    Boolean updateClientById(SysOauthClientDetailsDTO clientDetailsDTO);

    /**
     * 添加客户端
     * @param clientDetailsDTO
     * @return
     */
    Boolean saveClient(SysOauthClientDetailsDTO clientDetailsDTO);

    /**
     * 分页查询客户端信息
     * @param page
     * @param query
     * @return
     */
    Page queryPage(Page page, SysOauthClientDetails query);

    /**
     * 同步缓存 （清空缓存）
     * @return R
     */
    R syncClientCache();

}