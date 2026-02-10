package ltd.huntinginfo.feng.agent.service;

import ltd.huntinginfo.feng.agent.api.dto.AppKeyAuthRequest;
import ltd.huntinginfo.feng.agent.api.vo.AppKeyAuthResponse;

public interface AppAuthService {
    
    /**
     * 应用认证
     */
    AppKeyAuthResponse authenticateByAppKey(AppKeyAuthRequest request);
    
//    /**
//     * 获取应用信息
//     */
//    AppKeyAuthResponse getAppInfo(String appKey);
    
    /**
     * 刷新应用密钥
     */
    String refreshAppSecret(String appKey);

    /**
     * 应用系统的消息操作认证
     * @param request
     * @return
     */
	AppKeyAuthResponse authenticateMsgCenter(
    		String appKey, 
    		String signature,
    		Long timestamp,
    		String nonce);
}