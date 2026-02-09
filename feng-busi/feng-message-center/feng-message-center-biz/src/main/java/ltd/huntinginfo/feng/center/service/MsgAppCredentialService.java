package ltd.huntinginfo.feng.center.service;

import com.baomidou.mybatisplus.core.metadata.IPage;

import ltd.huntinginfo.feng.center.api.dto.MsgAppCredentialDTO;
import ltd.huntinginfo.feng.center.api.vo.MsgAppCredentialVO;

import java.util.List;

public interface MsgAppCredentialService {

    /**
     * 根据ID查询应用凭证
     */
	MsgAppCredentialVO getById(String id);

    /**
     * 根据appKey查询应用凭证
     */
    MsgAppCredentialVO getByAppKey(String appKey);

    /**
     * 分页查询应用凭证列表
     */
    IPage<MsgAppCredentialVO> page(IPage page, MsgAppCredentialDTO dto);

    /**
     * 查询应用凭证列表
     */
    List<MsgAppCredentialVO> list(MsgAppCredentialDTO dto);

    /**
     * 新增应用凭证
     */
    boolean save(MsgAppCredentialDTO dto);

    /**
     * 更新应用凭证
     */
    boolean updateById(MsgAppCredentialDTO dto);

    /**
     * 删除应用凭证
     */
    boolean removeById(String id);

    /**
     * 验证应用凭证有效性
     */
    boolean validateCredential(String appKey, String appSecret);

    /**
     * 获取密钥
     * @param appKey
     * @return
     */
	String getAppSecret(String appKey);
}
