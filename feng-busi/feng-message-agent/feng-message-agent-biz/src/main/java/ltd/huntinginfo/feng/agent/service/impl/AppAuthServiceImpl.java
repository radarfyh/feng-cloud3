package ltd.huntinginfo.feng.agent.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ltd.huntinginfo.feng.common.core.exception.BusinessException;
import ltd.huntinginfo.feng.agent.api.dto.AppKeyAuthRequest;
import ltd.huntinginfo.feng.agent.api.entity.MsgAppCredential;
import ltd.huntinginfo.feng.agent.api.entity.MsgAppPermission;
import ltd.huntinginfo.feng.agent.api.entity.MsgAuthLog;
import ltd.huntinginfo.feng.agent.api.utils.JwtUtil;
import ltd.huntinginfo.feng.agent.api.vo.AppAuthErrorCode;
import ltd.huntinginfo.feng.agent.api.vo.AppKeyAuthResponse;
import ltd.huntinginfo.feng.agent.mapper.MsgAppCredentialMapper;
import ltd.huntinginfo.feng.agent.mapper.MsgAppPermissionMapper;
import ltd.huntinginfo.feng.agent.service.AppAuthService;
import ltd.huntinginfo.feng.agent.service.MsgAuthLogService;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AppAuthServiceImpl implements AppAuthService {

    @Autowired
    private MsgAppCredentialMapper appCredentialMapper;
    @Autowired
    private MsgAppPermissionMapper appPermissionMapper;
    @Autowired
    private MsgAuthLogService authLogService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final long TIME_WINDOW = 5 * 60 * 1000; // 5分钟时间窗口
    private static final String NONCE_CACHE_PREFIX = "auth:nonce:";

    @Override
    public AppKeyAuthResponse authenticateByAppKey(AppKeyAuthRequest request) {
        AppKeyAuthResponse response = new AppKeyAuthResponse();
        
        try {
            // 1. 基础参数校验
            if (StrUtil.hasBlank(request.getAppKey(), request.getSignature(), request.getNonce()) ||
            		request.getTimestamp() == null) {
                return buildFailedResponse(response, AppAuthErrorCode.PARAM_MISSING, AppAuthErrorCode.PARAM_MISSING.getMessage());
            }
            
            // 2. 验证时间窗口
            long currentTime = System.currentTimeMillis();
            if (Math.abs(currentTime - request.getTimestamp()) > TIME_WINDOW) {
                return buildFailedResponse(response, AppAuthErrorCode.TIMESTAMP_EXPIRED, AppAuthErrorCode.TIMESTAMP_EXPIRED.getMessage());
            }
            
            // 3. 检查nonce唯一性(防重放攻击)
            String nonceKey = NONCE_CACHE_PREFIX + request.getNonce();
            if (Boolean.TRUE.equals(redisTemplate.hasKey(nonceKey))) {
                return buildFailedResponse(response, AppAuthErrorCode.REPLAY_ATTACK, AppAuthErrorCode.REPLAY_ATTACK.getMessage());
            }
            
            
            // 4. 查询调用者凭证
            MsgAppCredential callerCredential = appCredentialMapper.selectByAppKey(request.getCaller());
            if (callerCredential == null || !"0".equals(callerCredential.getDelFlag())) {
                return buildFailedResponse(response, AppAuthErrorCode.CALLER_INVALID, AppAuthErrorCode.CALLER_INVALID.getMessage());
            }
            
            // 5. 查询被调用者凭证
            MsgAppCredential credential = appCredentialMapper.selectByAppKey(request.getAppKey());
            if (credential == null || !"0".equals(credential.getDelFlag())) {
                return buildFailedResponse(response, AppAuthErrorCode.APPKEY_INVALID, AppAuthErrorCode.APPKEY_INVALID.getMessage());
            } else {
		        // 验证密钥            	
		        if (!verifySecret(request, response, credential.getAppSecret())) {
		            return response;
		        }
            }
            
            // 6. 验证应用状态
            if (credential.getStatus() != 1) {
                return buildFailedResponse(response, AppAuthErrorCode.APP_DISABLED, AppAuthErrorCode.APP_DISABLED.getMessage());
            }
            
            // 7. 验证有效期
            if (credential.getExpireTime() != null && credential.getExpireTime().before(new Date())) {
                return buildFailedResponse(response, AppAuthErrorCode.APP_EXPIRED, AppAuthErrorCode.APP_EXPIRED.getMessage());
            }
            
            // 8. 验证签名
            if (!verifySignature(request, callerCredential.getAppSecret())) {
                return buildFailedResponse(response, AppAuthErrorCode.SIGNATURE_INVALID, "签名验证失败");
            }
            
            // 9. 缓存nonce(5分钟有效期)
            redisTemplate.opsForValue().set(nonceKey, "1", TIME_WINDOW, TimeUnit.MILLISECONDS);
            
            // 10. 获取应用权限
            List<MsgAppPermission> permissions = appPermissionMapper.selectActivePermissions(request.getAppKey());
            List<String> permissionCodes = permissions.stream()
                    .map(MsgAppPermission::getResourceCode)
                    .collect(Collectors.toList());
            
            // 11. 生成令牌
            String token = jwtUtil.generateToken(credential.getId(), permissionCodes);
            
            // 12. 构建响应
            response.setSuccess(true);
            response.setAppId(credential.getId());
            response.setAppName(credential.getAppName());
            response.setAppKey(credential.getAppKey());
            response.setAgencyCode(credential.getAgencyCode());
            response.setPermissions(permissionCodes);
            response.setExpiresTime(jwtUtil.getExpireInSeconds());
            response.setToken(token);

//            // 2. 查询应用凭证
//            MsgAppCredential credential = appCredentialMapper.selectByAppKey(request.getAppKey());
//            if (credential == null || !"0".equals(credential.getDelFlag())) {
//                return buildFailedResponse(response, AppAuthErrorCode.APPKEY_INVALID, "无效的应用标识");
//            }
//
//
//            // 3. 验证应用状态
//            if (credential.getStatus() != 1) {
//                return buildFailedResponse(response, AppAuthErrorCode.APP_DISABLED, "应用已被禁用");
//            }
//
//            // 4. 验证密钥
//            if (!passwordEncoder.matches(request.getAppSecret(), credential.getAppSecret())) {
//                return buildFailedResponse(response, AppAuthErrorCode.SECRET_MISMATCH, "应用密钥验证失败");
//            }
//
//            // 5. 验证有效期
//            if (credential.getExpireTime() != null && credential.getExpireTime().before(new Date())) {
//                return buildFailedResponse(response, AppAuthErrorCode.APP_EXPIRED, "应用凭证已过期");
//            }
//
//            // 6. 获取应用权限
//            List<MsgAppPermission> permissions = appPermissionMapper.selectActivePermissions(request.getAppKey());
//            List<String> permissionCodes = permissions.stream()
//                    .map(MsgAppPermission::getResourceCode)
//                    .collect(Collectors.toList());
//
//            // 7. 生成令牌
//            String token = jwtUtil.generateToken(credential.getId(), permissionCodes);
//
//            // 8. 构建响应
//            response.setSuccess(true);
//            response.setAppId(Long.valueOf(credential.getId()));
//            response.setAppName(credential.getAppName());
//            response.setAppCode(credential.getAppKey());
//            response.setAgencyCode(credential.getAgencyCode());
//            response.setPermissions(permissionCodes);
//            response.setExpiresIn(jwtUtil.getExpireInSeconds());
//            response.setToken(token);
            
            log.info("应用认证成功 - appKey: {}", request.getAppKey());
        } catch (Exception e) {
            log.error("应用认证异常 - appKey: {}", request.getAppKey(), e);
            return buildFailedResponse(response, AppAuthErrorCode.SYSTEM_ERROR, AppAuthErrorCode.SYSTEM_ERROR.getMessage());
        }
        
        return response;
    }
    
    @Override
    public AppKeyAuthResponse authenticateMsgCenter(
    		String appKey, 
    		String signature,
    		Long timestamp,
    		String nonce) {
        AppKeyAuthResponse response = new AppKeyAuthResponse();
        
        try {
            // 1. 基础参数校验
            if (StrUtil.hasBlank(appKey, signature, nonce) ||
            		timestamp == null) {
                return buildFailedResponse(response, AppAuthErrorCode.PARAM_MISSING, AppAuthErrorCode.PARAM_MISSING.getMessage());
            }
            
            // 2. 验证时间窗口
            long currentTime = System.currentTimeMillis();
            if (Math.abs(currentTime - timestamp) > TIME_WINDOW) {
                return buildFailedResponse(response, AppAuthErrorCode.TIMESTAMP_EXPIRED, AppAuthErrorCode.TIMESTAMP_EXPIRED.getMessage());
            }
            
            // 3. 检查nonce唯一性(防重放攻击)
            String nonceKey = NONCE_CACHE_PREFIX + nonce;
            if (Boolean.TRUE.equals(redisTemplate.hasKey(nonceKey))) {
                return buildFailedResponse(response, AppAuthErrorCode.REPLAY_ATTACK, AppAuthErrorCode.REPLAY_ATTACK.getMessage());
            }
            
            // 5. 查询被调用者凭证
            MsgAppCredential credential = appCredentialMapper.selectByAppKey(appKey);
            if (credential == null || !"0".equals(credential.getDelFlag())) {
                return buildFailedResponse(response, AppAuthErrorCode.APPKEY_INVALID, AppAuthErrorCode.APPKEY_INVALID.getMessage());
            } 
            
            // 6. 验证应用状态
            if (credential.getStatus() != 1) {
                return buildFailedResponse(response, AppAuthErrorCode.APP_DISABLED, AppAuthErrorCode.APP_DISABLED.getMessage());
            }
            
            // 7. 验证有效期
            if (credential.getExpireTime() != null && credential.getExpireTime().before(new Date())) {
                return buildFailedResponse(response, AppAuthErrorCode.APP_EXPIRED, AppAuthErrorCode.APP_EXPIRED.getMessage());
            }
            
            // 8. 验证签名
            if (!verifySignatureForCenter(appKey, signature, timestamp, nonce, credential.getAppSecret())) {
                return buildFailedResponse(response, AppAuthErrorCode.SIGNATURE_INVALID, "签名验证失败");
            }
            
            // 9. 缓存nonce(5分钟有效期)
            redisTemplate.opsForValue().set(nonceKey, "1", TIME_WINDOW, TimeUnit.MILLISECONDS);
            
            // 10. 获取应用权限
//            List<MsgAppPermission> permissions = appPermissionMapper.selectActivePermissions(request.getAppKey());
//            List<String> permissionCodes = permissions.stream()
//                    .map(MsgAppPermission::getResourceCode)
//                    .collect(Collectors.toList());
            
            // 12. 构建响应
            response.setSuccess(true);
            response.setAppId(credential.getId());
            response.setAppName(credential.getAppName());
            response.setAppKey(credential.getAppKey());
            response.setAgencyCode(credential.getAgencyCode());
//            response.setPermissions(permissionCodes);
            response.setExpiresTime(jwtUtil.getExpireInSeconds());
           
            log.info("应用认证成功 - appKey: {}", appKey);
        } catch (Exception e) {
            log.error("应用认证异常 - appKey: {}", appKey, e);
            return buildFailedResponse(response, AppAuthErrorCode.SYSTEM_ERROR, AppAuthErrorCode.SYSTEM_ERROR.getMessage());
        }
        
        return response;
    }
    
    /**
     * 验证被调用者密钥
     */
    private Boolean verifySecret(AppKeyAuthRequest request, AppKeyAuthResponse response, String appSecret) {
        if (!passwordEncoder.matches(appSecret, request.getAppSecret())) {
            buildFailedResponse(response, AppAuthErrorCode.SECRET_MISMATCH, AppAuthErrorCode.SECRET_MISMATCH.getMessage());
            return false;
        }
        return true;
    }
    
    /**
     * 验证调用者签名
     */
    private Boolean verifySignature(AppKeyAuthRequest request, String appSecret) {
        // 构建签名字符串: appKey|timestamp|nonce|body_md5
    	String signContent = "";
    	if(StrUtil.isNotBlank(request.getBodyMd5())) {
	        signContent = String.format("%s|%d|%s|%s", 
	            request.getCaller(), 
	            request.getTimestamp(), 
	            request.getNonce(),
	            request.getBodyMd5());
    	} else {
	        signContent = String.format("%s|%d|%s", 
		            request.getCaller(), 
		            request.getTimestamp(), 
		            request.getNonce());
    	}
    	
    	log.info("signContent:{},getAppKey:{},getTimestamp:{},getNonce:{},getBodyMd5:{}", signContent, request.getAppKey(), request.getTimestamp(), request.getNonce(), request.getBodyMd5());
        
        // 使用HMAC-SHA256计算签名
        HMac hmac = new HMac(HmacAlgorithm.HmacSHA256, appSecret.getBytes());
        String calculatedSignature = hmac.digestHex(signContent);
        log.info("calculatedSignature:{},appSecret:{}, getSignature:{}, getCaller:{}", calculatedSignature, appSecret, request.getSignature(), request.getCaller());
        
        // 比对签名
        return calculatedSignature.equals(request.getSignature());
    }
    
    /**
     * 验证消息中心操作签名
     */
    private Boolean verifySignatureForCenter(
    		String appKey, 
    		String signature,
    		Long timestamp,
    		String nonce, 
    		String appSecret) {
        // 构建签名字符串: appKey|timestamp|nonce|body_md5
    	String signContent = String.format("%s|%d|%s", 
    			appKey, 
    			timestamp, 
    			nonce);
    	
    	log.info("signContent:{},appKey:{},timestamp:{},nonce:{}", signContent, appKey, timestamp, nonce);
        
        // 使用HMAC-SHA256计算签名
        HMac hmac = new HMac(HmacAlgorithm.HmacSHA256, appSecret.getBytes());
        String calculatedSignature = hmac.digestHex(signContent);
        log.info("calculatedSignature:{},appSecret:{}, getSignature:{}, getCaller:{}", calculatedSignature, appSecret, signature, appKey);
        
        // 比对签名
        return calculatedSignature.equals(signature);
    }

//    @Override
//    public AppKeyAuthResponse getAppInfo(String appKey) {
//        AppKeyAuthResponse response = new AppKeyAuthResponse();
//        
//        MsgAppCredential credential = appCredentialMapper.selectByAppKey(appKey);
//        if (credential == null || !"0".equals(credential.getDelFlag())) {
//            response.setSuccess(false);
//            response.setErrorMsg(AppAuthErrorCode.APP_NOT_EXISTED.getMessage());
//            return response;
//        }
//
//        response.setSuccess(true);
//        response.setAppId(Long.valueOf(credential.getId()));
//        response.setAppName(credential.getAppName());
//        response.setAppKey(credential.getAppKey());
//        response.setAgencyCode(credential.getAgencyCode());
//        return response;
//    }

    @Override
    public String refreshAppSecret(String appKey) {
        MsgAppCredential credential = appCredentialMapper.selectByAppKey(appKey);
        if (credential == null || !"0".equals(credential.getDelFlag())) {
            throw new BusinessException(Integer.valueOf(AppAuthErrorCode.APPKEY_INVALID.getCode()), AppAuthErrorCode.APPKEY_INVALID.getMessage());
        }

        String newSecret = UUID.randomUUID().toString().replace("-", "");
        
        // 存密文无法获取原始密钥，所以改为直接存 20250802
        //credential.setAppSecret(passwordEncoder.encode(newSecret));
        credential.setAppSecret(newSecret);
        appCredentialMapper.updateById(credential);
        
        return newSecret;
    }

    private AppKeyAuthResponse buildFailedResponse(AppKeyAuthResponse response, 
                                                 AppAuthErrorCode errorCode, 
                                                 String errorMsg) {
        response.setSuccess(false);
        response.setErrorMsg(errorMsg);
        
        // 记录认证失败日志
        MsgAuthLog authLog = new MsgAuthLog();
        authLog.setAppKey(response.getAppKey());
        authLog.setAuthType("APPKEY");
        authLog.setStatus(0);
        authLog.setErrorCode(errorCode.getCode());
        authLogService.save(authLog);
        
        return response;
    }
}