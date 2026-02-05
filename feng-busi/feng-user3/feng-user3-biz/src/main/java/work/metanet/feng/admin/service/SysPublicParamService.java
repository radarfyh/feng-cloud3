package work.metanet.feng.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.metanet.feng.admin.api.entity.SysPublicParam;

/**
 * 公共参数配置表(SysPublicParam)表服务接口
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
public interface SysPublicParamService extends IService<SysPublicParam> {

    /**
     * 通过key查询公共参数指定值
     * @param publicKey
     * @return
     */
    String getSysPublicParamKeyToValue(String publicKey);
}