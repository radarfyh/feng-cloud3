//package ltd.huntinginfo.feng.center.service.impl;
//
//import ltd.huntinginfo.feng.common.core.util.R;
//import ltd.huntinginfo.feng.center.api.entity.MsgAppCredential;
//import ltd.huntinginfo.feng.center.api.entity.MsgCenterToken;
//import ltd.huntinginfo.feng.center.api.entity.*;
//import ltd.huntinginfo.feng.center.api.utils.RestTemplateUtil;
//import ltd.huntinginfo.feng.center.service.*;
//import ltd.huntinginfo.feng.center.service.MessageCenterClientService;
//import ltd.huntinginfo.feng.center.service.MsgAgentLogService;
//import ltd.huntinginfo.feng.center.service.MsgCenterConfigService;
//import ltd.huntinginfo.feng.center.service.MsgCenterTokenService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//import cn.hutool.core.date.DateUtil;
//import cn.hutool.core.util.IdUtil;
//import cn.hutool.core.util.StrUtil;
//import cn.hutool.http.HttpRequest;
//import cn.hutool.http.HttpResponse;
//import cn.hutool.http.HttpStatus;
//import cn.hutool.http.Method;
//import cn.hutool.json.JSONArray;
//import cn.hutool.json.JSONObject;
//import cn.hutool.json.JSONUtil;
//
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Service;
//
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class MessageCenterClientServiceImpl implements MessageCenterClientService {
//
//    private final MsgCenterConfigService msgCenterConfigService;
//    private final MsgCenterTokenService msgCenterTokenService;
//    private final MsgAgentLogService msgAgentLogService;
//    
//    // 请求超时时间（毫秒）
//    private static final int REQUEST_TIMEOUT = 10000;
//    // 重试次数
//    private static final int MAX_RETRY_COUNT = 3;
//    
//    @Override
//    public CodeApplyResponse applyMessageCode(MsgAppCredential credential, CodeApplyRequest request) {
//        String operation = "申请消息编码";
//        String msgId = generateMsgId();
//        
//        try {
//            // 1. 准备请求
//            String apiUrl = buildApiUrl("api.code.apply");
//            String token = getValidToken(credential);
//            
//            // 2. 构建请求体
//            JSONObject requestBody = new JSONObject();
//            requestBody.set("sqssdm", request.getSqssdm());
//            
//            JSONObject sqdwxx = new JSONObject();
//            sqdwxx.set("sqdwdm", request.getSqdwxx().getSqdwdm());
//            sqdwxx.set("sqdwmc", request.getSqdwxx().getSqdwmc());
//            requestBody.set("sqdwxx", sqdwxx);
//            
//            JSONObject sqrxx = new JSONObject();
//            sqrxx.set("sqrxm", request.getSqrxx().getSqrxm());
//            sqrxx.set("sqrzjhm", request.getSqrxx().getSqrzjhm());
//            sqrxx.set("sqrdh", request.getSqrxx().getSqrdh());
//            requestBody.set("sqrxx", sqrxx);
//            
//            // 3. 发送请求
//            long startTime = System.currentTimeMillis();
//            HttpResponse response = sendHttpRequest(apiUrl, token, requestBody, operation);
//            long responseTime = System.currentTimeMillis() - startTime;
//            
//            // 4. 处理响应
//            CodeApplyResponse codeResponse = parseCodeApplyResponse(response);
//            
//            // 5. 记录日志
//            msgAgentLogService.logCallback(msgId, credential.getAppKey(), operation,
//                    "申请消息编码成功: " + codeResponse.getJzptjcbm(), 
//                    apiUrl, "POST", response.getStatus(), (int)responseTime, 
//                    buildLogDetail(request, codeResponse));
//            
//            // 6. 记录请求统计
//            msgCenterTokenService.recordRequest(credential.getAppKey(), operation, true);
//            
//            return codeResponse;
//            
//        } catch (Exception e) {
//            log.error("申请消息编码失败", e);
//            
//            // 记录错误日志
//            msgAgentLogService.logError(msgId, credential.getAppKey(), operation,
//                    "申请消息编码失败: " + e.getMessage(), null);
//            
//            // 记录请求统计
//            msgCenterTokenService.recordRequest(credential.getAppKey(), operation, false);
//            
//            // 返回错误响应
//            CodeApplyResponse errorResponse = new CodeApplyResponse();
//            errorResponse.setCode("99999");
//            errorResponse.setInfo("申请消息编码失败: " + e.getMessage());
//            return errorResponse;
//        }
//    }
//    
//    @Override
//    public MessageSendResponse sendMessages(MsgAppCredential credential, List<MessageSendRequest> requests) {
//        String operation = "发送消息";
//        String msgId = generateMsgId();
//        
//        try {
//            // 1. 准备请求
//            String apiUrl = buildApiUrl("api.message.send");
//            String token = getValidToken(credential);
//            
//            // 2. 构建请求体（数组格式）
//            JSONArray requestArray = new JSONArray();
//            for (MessageSendRequest request : requests) {
//                JSONObject message = new JSONObject();
//                message.set("fsdw", request.getFsdw());
//                message.set("fsdwdm", request.getFsdwdm());
//                message.set("fsr", request.getFsr());
//                message.set("fsrzjhm", request.getFsrzjhm());
//                message.set("fsdx", request.getFsdx());
//                message.set("jsdw", request.getJsdw());
//                message.set("jsdwdm", request.getJsdwdm());
//                message.set("jsr", request.getJsr());
//                message.set("jsrzjhm", request.getJsrzjhm());
//                message.set("ztbm", request.getZtbm());
//                message.set("xxbm", request.getXxbm());
//                message.set("xxlx", request.getXxlx());
//                message.set("xxbt", request.getXxbt());
//                message.set("xxnr", request.getXxnr());
//                message.set("cldz", request.getCldz());
//                message.set("jjcd", request.getJjcd());
//                message.set("ywcs", request.getYwcs());
//                message.set("tb", request.getTb());
//                requestArray.add(message);
//            }
//            
//            // 3. 发送请求
//            long startTime = System.currentTimeMillis();
//            HttpResponse response = sendHttpRequest(apiUrl, token, requestArray, operation);
//            long responseTime = System.currentTimeMillis() - startTime;
//            
//            // 4. 处理响应
//            MessageSendResponse sendResponse = parseMessageSendResponse(response);
//            
//            // 5. 记录日志
//            msgAgentLogService.logCallback(msgId, credential.getAppKey(), operation,
//                    "发送消息成功，数量: " + requests.size(), 
//                    apiUrl, "POST", response.getStatus(), (int)responseTime, 
//                    buildLogDetail(requests, sendResponse));
//            
//            // 6. 记录请求统计
//            msgCenterTokenService.recordRequest(credential.getAppKey(), operation, true);
//            
//            return sendResponse;
//            
//        } catch (Exception e) {
//            log.error("发送消息失败", e);
//            
//            // 记录错误日志
//            msgAgentLogService.logError(msgId, credential.getAppKey(), operation,
//                    "发送消息失败: " + e.getMessage(), null);
//            
//            // 记录请求统计
//            msgCenterTokenService.recordRequest(credential.getAppKey(), operation, false);
//            
//            // 返回错误响应
//            MessageSendResponse errorResponse = new MessageSendResponse();
//            errorResponse.setCode("99999");
//            errorResponse.setInfo("发送消息失败: " + e.getMessage());
//            return errorResponse;
//        }
//    }
//    
//    @Override
//    public MessageReceiveResponse receiveMessages(MsgAppCredential credential, MessageReceiveRequest request) {
//        String operation = "接收消息";
//        String msgId = generateMsgId();
//        
//        try {
//            // 1. 准备请求
//            String apiUrl = buildApiUrl("api.message.receive");
//            String token = getValidToken(credential);
//            
//            // 2. 构建请求体
//            JSONObject requestBody = new JSONObject();
//            if (StrUtil.isNotBlank(request.getYbid())) {
//                requestBody.set("ybid", request.getYbid());
//            }
//            if (StrUtil.isNotBlank(request.getZtbm())) {
//                requestBody.set("ztbm", request.getZtbm());
//            }
//            
//            // 3. 发送请求
//            long startTime = System.currentTimeMillis();
//            HttpResponse response = sendHttpRequest(apiUrl, token, requestBody, operation);
//            long responseTime = System.currentTimeMillis() - startTime;
//            
//            // 4. 处理响应
//            MessageReceiveResponse receiveResponse = parseMessageReceiveResponse(response);
//            
//            // 5. 记录日志
//            int messageCount = receiveResponse.getXxjl() != null ? receiveResponse.getXxjl().size() : 0;
//            msgAgentLogService.logCallback(msgId, credential.getAppKey(), operation,
//                    "接收消息成功，数量: " + messageCount + ", ybid: " + receiveResponse.getYbid(), 
//                    apiUrl, "POST", response.getStatus(), (int)responseTime, 
//                    buildLogDetail(request, receiveResponse));
//            
//            // 6. 记录请求统计
//            msgCenterTokenService.recordRequest(credential.getAppKey(), operation, true);
//            
//            return receiveResponse;
//            
//        } catch (Exception e) {
//            log.error("接收消息失败", e);
//            
//            // 记录错误日志
//            msgAgentLogService.logError(msgId, credential.getAppKey(), operation,
//                    "接收消息失败: " + e.getMessage(), null);
//            
//            // 记录请求统计
//            msgCenterTokenService.recordRequest(credential.getAppKey(), operation, false);
//            
//            // 返回错误响应
//            MessageReceiveResponse errorResponse = new MessageReceiveResponse();
//            errorResponse.setCode("99999");
//            errorResponse.setInfo("接收消息失败: " + e.getMessage());
//            return errorResponse;
//        }
//    }
//    
//    @Override
//    public MessageStatusUpdateResponse updateMessageStatus(MsgAppCredential credential, MessageStatusUpdateRequest request) {
//        String operation = "更新消息状态";
//        String msgId = generateMsgId();
//        
//        try {
//            // 1. 准备请求
//            String apiUrl = buildApiUrl("api.message.status.update");
//            String token = getValidToken(credential);
//            
//            // 2. 构建请求体
//            JSONObject requestBody = new JSONObject();
//            requestBody.set("cldw", request.getCldw());
//            requestBody.set("cldwdm", request.getCldwdm());
//            requestBody.set("clr", request.getClr());
//            requestBody.set("clrzjhm", request.getClrzjhm());
//            requestBody.set("xxbm", request.getXxbm());
//            
//            // 3. 发送请求
//            long startTime = System.currentTimeMillis();
//            HttpResponse response = sendHttpRequest(apiUrl, token, requestBody, operation);
//            long responseTime = System.currentTimeMillis() - startTime;
//            
//            // 4. 处理响应
//            MessageStatusUpdateResponse statusResponse = parseMessageStatusUpdateResponse(response);
//            
//            // 5. 记录日志
//            msgAgentLogService.logCallback(msgId, credential.getAppKey(), operation,
//                    "更新消息状态成功: " + request.getXxbm(), 
//                    apiUrl, "POST", response.getStatus(), (int)responseTime, 
//                    buildLogDetail(request, statusResponse));
//            
//            // 6. 记录请求统计
//            msgCenterTokenService.recordRequest(credential.getAppKey(), operation, true);
//            
//            return statusResponse;
//            
//        } catch (Exception e) {
//            log.error("更新消息状态失败", e);
//            
//            // 记录错误日志
//            msgAgentLogService.logError(msgId, credential.getAppKey(), operation,
//                    "更新消息状态失败: " + e.getMessage(), null);
//            
//            // 记录请求统计
//            msgCenterTokenService.recordRequest(credential.getAppKey(), operation, false);
//            
//            // 返回错误响应
//            MessageStatusUpdateResponse errorResponse = new MessageStatusUpdateResponse();
//            errorResponse.setCode("99999");
//            errorResponse.setInfo("更新消息状态失败: " + e.getMessage());
//            return errorResponse;
//        }
//    }
//    
//    @Override
//    public UnreadMessageResponse queryUnreadMessages(MsgAppCredential credential, UnreadMessageRequest request) {
//        String operation = "查询未读消息";
//        String msgId = generateMsgId();
//        
//        try {
//            // 1. 准备请求
//            String apiUrl = buildApiUrl("api.message.unread");
//            String token = getValidToken(credential);
//            
//            // 2. 构建请求体
//            JSONObject requestBody = new JSONObject();
//            if (request.getCxdwxx() != null) {
//                JSONObject cxdwxx = new JSONObject();
//                cxdwxx.set("cxdw", request.getCxdwxx().getCxdw());
//                cxdwxx.set("cxdwdm", request.getCxdwxx().getCxdwdm());
//                requestBody.set("cxdwxx", cxdwxx);
//            }
//            if (request.getCxrxx() != null) {
//                JSONObject cxrxx = new JSONObject();
//                cxrxx.set("cxr", request.getCxrxx().getCxr());
//                cxrxx.set("cxrzjhm", request.getCxrxx().getCxrzjhm());
//                requestBody.set("cxrxx", cxrxx);
//            }
//            
//            // 3. 发送请求
//            long startTime = System.currentTimeMillis();
//            HttpResponse response = sendHttpRequest(apiUrl, token, requestBody, operation);
//            long responseTime = System.currentTimeMillis() - startTime;
//            
//            // 4. 处理响应
//            UnreadMessageResponse unreadResponse = parseMessageUnreadResponse(response);
//            
//            // 5. 记录日志
//            msgAgentLogService.logCallback(msgId, credential.getAppKey(), operation,
//                    "查询未读消息成功，数量: " + unreadResponse.getXxzs(), 
//                    apiUrl, "POST", response.getStatus(), (int)responseTime, 
//                    buildLogDetail(request, unreadResponse));
//            
//            // 6. 记录请求统计
//            msgCenterTokenService.recordRequest(credential.getAppKey(), operation, true);
//            
//            return unreadResponse;
//            
//        } catch (Exception e) {
//            log.error("查询未读消息失败", e);
//            
//            // 记录错误日志
//            msgAgentLogService.logError(msgId, credential.getAppKey(), operation,
//                    "查询未读消息失败: " + e.getMessage(), null);
//            
//            // 记录请求统计
//            msgCenterTokenService.recordRequest(credential.getAppKey(), operation, false);
//            
//            // 返回错误响应
//            UnreadMessageResponse errorResponse = new UnreadMessageResponse();
//            errorResponse.setCode("99999");
//            errorResponse.setInfo("查询未读消息失败: " + e.getMessage());
//            return errorResponse;
//        }
//    }
//
//    
//    @Override
//    public boolean refreshToken(MsgAppCredential credential) {
//        String operation = "刷新Token";
//        String msgId = generateMsgId();
//        
//        try {
//            log.info("开始刷新Token: appKey={}", credential.getAppKey());
//            
//            // 1. 模拟Token刷新（实际应该调用认证接口）
//            String newToken = "MOCK_TOKEN_" + System.currentTimeMillis() + "_" + credential.getAppKey();
//            Date expireTime = new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(2));
//            
//            // 2. 更新Token记录
//            MsgCenterToken tokenRecord = msgCenterTokenService.createOrUpdateToken(
//                credential.getAppKey(), newToken, expireTime);
//            
//            // 3. 更新应用凭证中的Token
//            if (tokenRecord != null) {
//                credential.setCenterToken(newToken);
//                credential.setCenterExpireTime(expireTime);
//                
//                // 记录日志
//                msgAgentLogService.logCallback(msgId, credential.getAppKey(), operation,
//                        "Token刷新成功，过期时间: " + DateUtil.formatDateTime(expireTime), 
//                        null, null, null, null, null);
//                
//                log.info("Token刷新成功: appKey={}, expireTime={}", 
//                        credential.getAppKey(), DateUtil.formatDateTime(expireTime));
//                return true;
//            }
//            
//            return false;
//            
//        } catch (Exception e) {
//            log.error("刷新Token失败: appKey={}", credential.getAppKey(), e);
//            
//            // 记录错误日志
//            msgAgentLogService.logError(msgId, credential.getAppKey(), operation,
//                    "刷新Token失败: " + e.getMessage(), null);
//            
//            return false;
//        }
//    }
//    
//    @Override
//    public boolean isTokenValid(MsgAppCredential credential) {
//        try {
//            return msgCenterTokenService.isTokenValid(credential.getAppKey());
//        } catch (Exception e) {
//            log.error("检查Token有效性失败: appKey={}", credential.getAppKey(), e);
//            return false;
//        }
//    }
//    
//    @Override
//    public String getBaseUrl() {
//        return buildBaseUrl();
//    }
//    
//    @Override
//    public boolean isMockEnabled() {
//        return msgCenterConfigService.isMockEnabled();
//    }
//    
//    // ========== 私有工具方法 ==========
//    
//    /**
//     * 构建完整的API URL
//     */
//    private String buildApiUrl(String apiKey) {
//        String baseUrl = buildBaseUrl();
//        String apiPath = msgCenterConfigService.getConfigValue(apiKey);
//        return baseUrl + apiPath;
//    }
//    
//    /**
//     * 构建基础URL
//     */
//    private String buildBaseUrl() {
//        if (isMockEnabled()) {
//            return msgCenterConfigService.getConfigValue("center.mock.base.url") + 
//                   msgCenterConfigService.getConfigValue("center.resource");
//        } else {
//            return msgCenterConfigService.getConfigValue("center.base.url") + "/" + 
//                   msgCenterConfigService.getConfigValue("center.resource");
//        }
//    }
//    
//    /**
//     * 获取有效的Token
//     */
//    @Override
//    public String getValidToken(MsgAppCredential credential) {
//        try {
//            // 检查Token是否有效
//            if (!isTokenValid(credential)) {
//                // Token无效，尝试刷新
//                refreshToken(credential);
//            }
//            
//            // 从应用凭证获取Token
//            String token = credential.getCenterToken();
//            if (StrUtil.isBlank(token)) {
//                // 从Token表获取
//                MsgCenterToken tokenRecord = msgCenterTokenService.getByAppKey(credential.getAppKey());
//                if (tokenRecord != null) {
//                    token = tokenRecord.getCenterToken();
//                }
//            }
//            
//            if (StrUtil.isBlank(token)) {
//                throw new RuntimeException("Token为空，请先获取Token");
//            }
//            
//            return token;
//            
//        } catch (Exception e) {
//            log.error("获取有效Token失败: appKey={}", credential.getAppKey(), e);
//            throw new RuntimeException("获取Token失败: " + e.getMessage());
//        }
//    }
//    
//    /**
//     * 发送HTTP请求
//     */
//    private HttpResponse sendHttpRequest(String url, String token, Object body, String operation) {
//        HttpRequest request = HttpRequest.of(url)
//                .method(Method.POST)
//                .header("Content-Type", "application/json")
//                .header("Token", token)
//                .body(JSONUtil.toJsonStr(body))
//                .timeout(REQUEST_TIMEOUT);
//        
//        // 重试机制
//        for (int i = 0; i < MAX_RETRY_COUNT; i++) {
//            try {
//                HttpResponse response = request.execute();
//                
//                if (response.isOk()) {
//                    return response;
//                } else if (response.getStatus() == HttpStatus.HTTP_UNAUTHORIZED) {
//                    // Token失效，需要刷新Token
//                    log.warn("Token失效，需要刷新: status={}", response.getStatus());
//                    // 这里可以触发Token刷新逻辑
//                    throw new RuntimeException("Token失效，状态码: " + response.getStatus());
//                } else if (i < MAX_RETRY_COUNT - 1) {
//                    // 重试
//                    log.warn("请求失败，重试 {}/{}: url={}, status={}", 
//                            i + 1, MAX_RETRY_COUNT, url, response.getStatus());
//                    try {
//                        Thread.sleep(1000 * (i + 1)); // 指数退避
//                    } catch (InterruptedException ie) {
//                        Thread.currentThread().interrupt();
//                        break;
//                    }
//                } else {
//                    throw new RuntimeException("请求失败，状态码: " + response.getStatus() + 
//                            ", 响应: " + response.body());
//                }
//                
//            } catch (Exception e) {
//                if (i < MAX_RETRY_COUNT - 1) {
//                    log.warn("请求异常，重试 {}/{}: url={}, error={}", 
//                            i + 1, MAX_RETRY_COUNT, url, e.getMessage());
//                    try {
//                        Thread.sleep(1000 * (i + 1));
//                    } catch (InterruptedException ie) {
//                        Thread.currentThread().interrupt();
//                        break;
//                    }
//                } else {
//                    throw new RuntimeException("请求失败: " + e.getMessage(), e);
//                }
//            }
//        }
//        
//        throw new RuntimeException("请求失败，已达到最大重试次数");
//    }
//    
//    /**
//     * 解析申请消息编码响应
//     */
//    private CodeApplyResponse parseCodeApplyResponse(HttpResponse response) {
//        try {
//            String body = response.body();
//            JSONObject json = JSONUtil.parseObj(body);
//            
//            CodeApplyResponse result = new CodeApplyResponse();
////            result.setStatus(json.getStr("status"));
////            result.setMessage(json.getStr("message"));
//            
//            JSONObject data = json.getJSONObject("data");
//            if (data != null) {
//                result.setCode(data.getStr("code"));
//                result.setInfo(data.getStr("info"));
//                result.setJzptjcbm(data.getStr("jzptjcbm"));
//                result.setEwm(data.getStr("ewm"));
//            }
//            
//            return result;
//        } catch (Exception e) {
//            throw new RuntimeException("解析响应失败: " + e.getMessage(), e);
//        }
//    }
//    
//    /**
//     * 解析发送消息响应
//     */
//    private MessageSendResponse parseMessageSendResponse(HttpResponse response) {
//        try {
//            String body = response.body();
//            JSONObject json = JSONUtil.parseObj(body);
//            
//            MessageSendResponse result = new MessageSendResponse();
////            result.setStatus(json.getStr("status"));
////            result.setMessage(json.getStr("message"));
//            
//            JSONObject data = json.getJSONObject("data");
//            if (data != null) {
//                result.setCode(data.getStr("code"));
//                result.setInfo(data.getStr("info"));
//            }
//            
//            return result;
//        } catch (Exception e) {
//            throw new RuntimeException("解析响应失败: " + e.getMessage(), e);
//        }
//    }
//    
//    /**
//     * 解析接收消息响应
//     */
//    private MessageReceiveResponse parseMessageReceiveResponse(HttpResponse response) {
//        try {
//            String body = response.body();
//            JSONObject json = JSONUtil.parseObj(body);
//            
//            MessageReceiveResponse result = new MessageReceiveResponse();
////            result.setStatus(json.getStr("status"));
////            result.setMessage(json.getStr("message"));
//            
//            JSONObject data = json.getJSONObject("data");
//            if (data != null) {
//                result.setCode(data.getStr("code"));
//                result.setInfo(data.getStr("info"));
//                result.setYbid(data.getStr("ybid"));
//                
//                JSONArray xxjl = data.getJSONArray("xxjl");
//                if (xxjl != null) {
//                    List<MessageRecord> records = JSONUtil.toList(xxjl, MessageRecord.class);
//                    result.setXxjl(records);
//                }
//            }
//            
//            return result;
//        } catch (Exception e) {
//            throw new RuntimeException("解析响应失败: " + e.getMessage(), e);
//        }
//    }
//    
//    /**
//     * 解析更新消息状态响应
//     */
//    private MessageStatusUpdateResponse parseMessageStatusUpdateResponse(HttpResponse response) {
//        try {
//            String body = response.body();
//            JSONObject json = JSONUtil.parseObj(body);
//            
//            MessageStatusUpdateResponse result = new MessageStatusUpdateResponse();
////            result.setStatus(json.getStr("status"));
////            result.setMessage(json.getStr("message"));
//            
//            JSONObject data = json.getJSONObject("data");
//            if (data != null) {
//                result.setCode(data.getStr("code"));
//                result.setInfo(data.getStr("info"));
//            }
//            
//            return result;
//        } catch (Exception e) {
//            throw new RuntimeException("解析响应失败: " + e.getMessage(), e);
//        }
//    }
//    
//    /**
//     * 解析查询未读消息响应
//     */
//    private UnreadMessageResponse parseMessageUnreadResponse(HttpResponse response) {
//        try {
//            String body = response.body();
//            JSONObject json = JSONUtil.parseObj(body);
//            
////            CenterResponse res = new CenterResponse();
////            res.setStatus(json.getStr("status"));
////            res.setMessage(json.getStr("message"));
//            
//            JSONObject data = json.getJSONObject("data");
////            res.setData(data);
//            
//            UnreadMessageResponse result = new UnreadMessageResponse();
//            
//            if (data != null) {
//
//                String code = data.getStr("code");
//                result.setCode(code);
//                String info = data.getStr("info");
//                result.setInfo(info);
//                Integer total = data.getInt("xxzs");
//                result.setXxzs(total);
//                
//                JSONArray xxjl = data.getJSONArray("xxjl");
//                if (xxjl != null) {
//                    List<MessageRecord> records = JSONUtil.toList(xxjl, MessageRecord.class);
//                    result.setXxjl(records);
//                }
//            }
//            
//            return result;
//        } catch (Exception e) {
//            throw new RuntimeException("解析响应失败: " + e.getMessage(), e);
//        }
//    }
//    
//    /**
//     * 生成代理平台消息ID
//     */
//    private String generateMsgId() {
//        return IdUtil.fastSimpleUUID();
//    }
//    
//    /**
//     * 构建日志详情
//     */
//    private Object buildLogDetail(Object request, Object response) {
//        JSONObject detail = new JSONObject();
//        detail.set("request", request);
//        detail.set("response", response);
//        detail.set("timestamp", System.currentTimeMillis());
//        return detail;
//    }
//}