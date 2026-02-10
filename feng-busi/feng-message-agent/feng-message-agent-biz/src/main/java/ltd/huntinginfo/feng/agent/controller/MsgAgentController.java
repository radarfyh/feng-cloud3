package ltd.huntinginfo.feng.agent.controller;

import ltd.huntinginfo.feng.common.core.util.R;
import ltd.huntinginfo.feng.agent.api.dto.*;
import ltd.huntinginfo.feng.agent.api.entity.*;
import ltd.huntinginfo.feng.agent.api.vo.*;
import ltd.huntinginfo.feng.agent.config.MsgSendQueueConfig;
import ltd.huntinginfo.feng.agent.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "消息代理平台控制器", description = "为业务系统提供消息中心代理服务")
@Slf4j
@Validated
@RestController
@RequestMapping("/msg/AgentCenter")
@RequiredArgsConstructor
public class MsgAgentController {
    
    // 依赖注入
    private final MessageCenterClientService messageCenterClientService;
    private final MsgAppCredentialService msgAppCredentialService;
    private final MsgAgentMappingService msgAgentMappingService;
    private final MsgStatusCodeService msgStatusCodeService;
    private final MsgAgentLogService msgAgentLogService;
    private final AppAuthService appAuthService;
    private final MessagePollingService messagePollingService;
    private final MsgSendQueueService msgSendQueueService;
    private final MsgSendQueueConfig msgSendQueueConfig;
    
    /**
     * MSG-1000: 消息发送接口
     */
    @Operation(summary = "消息发送", description = "接收业务系统消息，申请消息编码，转发到部级消息中心")
    @PostMapping("/sendMsg")
    public R<MsgSendResponseVO> sendMessage(
            @Parameter(description = "应用系统编码", required = true) 
            @RequestHeader("X-App-Key") String appKey,
            @Parameter(description = "签名信息", required = true) 
            @RequestHeader("X-Signature") String signature,
            @Parameter(description = "时间戳(毫秒)", required = true) 
            @RequestHeader("X-Timestamp") Long timestamp,
            @Parameter(description = "随机数", required = true) 
            @RequestHeader("X-Nonce") String nonce,
            @Valid @RequestBody MsgSendRequestDTO requestDTO) {
        
        String requestId = IdUtil.fastSimpleUUID();
        
        try {
            log.info("[{}] 收到消息发送请求，业务ID: {}, 系统: {}", 
                    requestId, requestDTO.getBizId(), appKey);
            
            // 1. 身份验证
            AppKeyAuthResponse authResponse = appAuthService.authenticateMsgCenter(
                appKey, signature, timestamp, nonce);
            if (!authResponse.isSuccess()) {
                log.warn("[{}] 身份验证失败: {}", requestId, authResponse.getErrorMsg());
                return R.failed(authResponse.getErrorMsg());
            }
            
            // 2. 幂等性检查
            MsgAgentMapping existingMapping = msgAgentMappingService.getByAppKeyAndBizId(
                appKey, requestDTO.getBizId());
            if (existingMapping != null) {
                log.info("[{}] 重复请求，返回已存在的消息记录", requestId);
                return R.ok(convertToResponseVO(existingMapping));
            }
            
            // 3. 获取应用凭证
            MsgAppCredentialVO appCredential = msgAppCredentialService.getByAppKey(appKey);
            if (appCredential == null) {
                return R.failed("应用凭证不存在或已禁用");
            }
            
            // 4. 申请消息编码
            CodeApplyRequest codeRequest = buildCodeApplyRequest(requestDTO, appCredential);
            CodeApplyResponse codeResponse = messageCenterClientService.applyMessageCode(
                convertToEntity(appCredential), codeRequest);
            
            if (!"00000".equals(codeResponse.getCode())) {
                log.error("[{}] 申请消息编码失败: {}", requestId, codeResponse.getInfo());
                return R.failed("申请消息编码失败: " + codeResponse.getInfo());
            }
            
            // 5. 转换为部级消息格式并发送
            List<MessageSendRequest> centerRequests = buildCenterMessageRequests(
                requestDTO, codeResponse, appCredential);
            MessageSendResponse sendResponse = messageCenterClientService.sendMessages(
                convertToEntity(appCredential), centerRequests);
            
            if (!"00000".equals(sendResponse.getCode())) {
                log.error("[{}] 发送到消息中心失败: {}", requestId, sendResponse.getInfo());
                return R.failed("发送到消息中心失败: " + sendResponse.getInfo());
            }
            
            // 6. 保存消息映射记录
            MsgAgentMapping mapping = saveMessageMapping(
                requestDTO, appCredential, codeResponse);
            
            // 7. 异步处理回调队列
            scheduleCallbackTask(mapping);
            
            // 8. 记录日志
            msgAgentLogService.logSend(
                mapping.getId(), appKey,
                "SEND_MESSAGE",
                "消息发送成功",
                Map.of("bizId", requestDTO.getBizId(), "xxbm", mapping.getXxbm())
            );
            
            // 9. 返回响应
            MsgSendResponseVO responseVO = convertToResponseVO(mapping);
            log.info("[{}] 消息发送成功，代理消息ID: {}, 部级消息编码: {}", 
                    requestId, responseVO.getMsgId(), responseVO.getXxbm());
            
            return R.ok(responseVO, "消息发送成功");
            
        } catch (IllegalArgumentException e) {
            log.error("[{}] 参数验证失败: {}", requestId, e.getMessage(), e);
            return R.failed(e.getMessage());
        } catch (Exception e) {
            log.error("[{}] 消息发送系统异常", requestId, e);
            return R.failed("系统内部错误: " + e.getMessage());
        }
    }
    
    /**
     * MSG-1011: 消息查询接口
     */
    @Operation(summary = "消息查询", description = "业务系统向代理平台查询消息")
    @PostMapping("/queryMsg")
    public R<List<MsgQueryResponseVO>> queryMessages(
            @Parameter(description = "应用系统编码", required = true) 
            @RequestHeader("X-App-Key") String appKey,
            @Parameter(description = "签名信息", required = true) 
            @RequestHeader("X-Signature") String signature,
            @Parameter(description = "时间戳(毫秒)", required = true) 
            @RequestHeader("X-Timestamp") Long timestamp,
            @Parameter(description = "随机数", required = true) 
            @RequestHeader("X-Nonce") String nonce,
            @Valid @RequestBody MsgQueryRequestDTO requestDTO) {
        
        try {
            log.info("收到消息查询请求，应用: {}, 类型: {}", appKey, requestDTO.getType());
            
            // 1. 身份验证
            AppKeyAuthResponse authResponse = appAuthService.authenticateMsgCenter(
                appKey, signature, timestamp, nonce);
            if (!authResponse.isSuccess()) {
                return R.failed(authResponse.getErrorMsg());
            }
            
            // 2. 构建查询条件
            MsgAgentMapping query = new MsgAgentMapping();
            query.setAppKey(appKey);
            
            List<MsgAgentMapping> messages;
            int limit = requestDTO.getLimit() != null ? requestDTO.getLimit() : 20;
            
            if ("1".equals(requestDTO.getType())) {
                // 查询最近收到的消息
                messages = msgAgentMappingService.getRecentMessages(appKey, limit);
            } else if ("2".equals(requestDTO.getType())) {
                // 查询未读消息
                messages = msgAgentMappingService.getUnreadMessages(appKey);
            } else {
                return R.failed("不支持的查询类型: " + requestDTO.getType());
            }
            
            // 3. 转换为响应格式
            List<MsgQueryResponseVO> responseList = convertToQueryResponse(messages);
            
            log.info("查询成功，返回消息数量: {}", responseList.size());
            return R.ok(responseList, "查询成功");
            
        } catch (Exception e) {
            log.error("消息查询失败: appKey={}", appKey, e);
            return R.failed("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * MSG-1020: 消息状态更新接口
     */
    @Operation(summary = "消息状态更新", description = "业务系统处理消息后，通过代理平台更新部级消息中心的消息状态")
    @PostMapping("/updateMsgStatus")
    public R<Void> updateMessageStatus(
            @Parameter(description = "应用系统编码", required = true) 
            @RequestHeader("X-App-Key") String appKey,
            @Parameter(description = "签名信息", required = true) 
            @RequestHeader("X-Signature") String signature,
            @Parameter(description = "时间戳(毫秒)", required = true) 
            @RequestHeader("X-Timestamp") Long timestamp,
            @Parameter(description = "随机数", required = true) 
            @RequestHeader("X-Nonce") String nonce,
            @Valid @RequestBody MsgStatusUpdateRequestDTO requestDTO) {
        
        try {
            log.info("收到状态更新请求，消息编码: {}, 应用: {}", requestDTO.getXxbm(), appKey);
            
            // 1. 身份验证
            AppKeyAuthResponse authResponse = appAuthService.authenticateMsgCenter(
                appKey, signature, timestamp, nonce);
            if (!authResponse.isSuccess()) {
                return R.failed(authResponse.getErrorMsg());
            }
            
            // 2. 查找消息映射记录
            MsgAgentMapping mapping = msgAgentMappingService.getByXxbm(requestDTO.getXxbm());
            if (mapping == null) {
                return R.failed("消息记录不存在");
            }
            
            // 3. 权限验证
            if (!mapping.getAppKey().equals(appKey)) {
                return R.failed("无权操作此消息");
            }
            
            // 4. 调用消息中心状态更新服务
            MsgAppCredentialVO appCredential = msgAppCredentialService.getByAppKey(appKey);
            MessageStatusUpdateRequest centerRequest = buildCenterStatusUpdateRequest(requestDTO);
            MessageStatusUpdateResponse centerResponse = messageCenterClientService.updateMessageStatus(
                convertToEntity(appCredential), centerRequest);
            
            if (!"00000".equals(centerResponse.getCode())) {
                log.error("更新消息状态失败: {}", centerResponse.getInfo());
                return R.failed("更新消息状态失败: " + centerResponse.getInfo());
            }
            
            // 5. 更新本地消息状态
            mapping.setStatus("COMPLETED");
            mapping.setStatusCode("1007");
            mapping.setCompleteTime(new Date());
            mapping.setCenterClzt("1");
            msgAgentMappingService.updateById(mapping);
            
            // 6. 记录日志
            msgAgentLogService.logStatus(
                mapping.getId(), appKey,
                "UPDATE_STATUS",
                mapping.getStatus(), "COMPLETED",
                "消息状态更新为已完成"
            );
            
            log.info("消息状态更新成功: xxbm={}", requestDTO.getXxbm());
            return R.ok(null, "消息状态更新成功");
            
        } catch (Exception e) {
            log.error("状态更新失败: xxbm={}", requestDTO.getXxbm(), e);
            return R.failed("状态更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 消息分页查询接口
     */
    @Operation(summary = "消息分页查询", description = "分页查询消息记录")
    @GetMapping("/page")
    public R<IPage<MsgAgentMappingPageVO>> pageMessages(
            @Parameter(description = "应用系统编码", required = true)
            @RequestHeader("X-App-Key") String appKey,
            @Parameter(description = "签名信息", required = true)
            @RequestHeader("X-Signature") String signature,
            @Parameter(description = "时间戳(毫秒)", required = true)
            @RequestHeader("X-Timestamp") Long timestamp,
            @Parameter(description = "随机数", required = true)
            @RequestHeader("X-Nonce") String nonce,
            @Parameter(description = "页码", required = false)
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量", required = false)
            @RequestParam(defaultValue = "20") Integer pageSize,
            @Parameter(description = "查询条件", required = false)
            @RequestBody(required = false) MsgAgentMappingPageDTO queryDTO) {
        
        try {
            // 1. 身份验证
            AppKeyAuthResponse authResponse = appAuthService.authenticateMsgCenter(
                appKey, signature, timestamp, nonce);
            if (!authResponse.isSuccess()) {
                return R.failed(authResponse.getErrorMsg());
            }
            
            // 2. 设置应用标识
            if (queryDTO == null) {
                queryDTO = new MsgAgentMappingPageDTO();
            }
            queryDTO.setAppKey(appKey);
            
            // 3. 分页查询
            Page<MsgAgentMapping> page = new Page<>(pageNum, pageSize);
            MsgAgentMapping mapping = new MsgAgentMapping();
            BeanUtil.copyProperties(mapping, queryDTO);
            IPage<MsgAgentMapping> entityResult = msgAgentMappingService.page(page, mapping);
            
            // 5. 转换为VO分页
            IPage<MsgAgentMappingPageVO> voResult = entityResult.convert(entity -> {
            	MsgAgentMappingPageVO vo = new MsgAgentMappingPageVO();
                BeanUtil.copyProperties(entity, vo);
                
                // 添加格式化后的时间字段
                if (entity.getSendTime() != null) {
                    vo.setSendTimeStr(DateUtil.formatDateTime(entity.getSendTime()));
                }
                if (entity.getCompleteTime() != null) {
                    vo.setCompleteTimeStr(DateUtil.formatDateTime(entity.getCompleteTime()));
                }
                if (entity.getCreateTime() != null) {
                    vo.setCreateTimeStr(DateUtil.formatDateTime(entity.getCreateTime()));
                }
                
                return vo;
            });
            
            return R.ok(voResult, "查询成功");
            
        } catch (Exception e) {
            log.error("分页查询失败", e);
            return R.failed("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取消息详情
     */
    @Operation(summary = "获取消息详情", description = "根据代理消息ID获取消息详情")
    @GetMapping("/detail/{msgId}")
    public R<MsgAgentMappingVO> getMessageDetail(
            @Parameter(description = "应用系统编码", required = true)
            @RequestHeader("X-App-Key") String appKey,
            @Parameter(description = "签名信息", required = true)
            @RequestHeader("X-Signature") String signature,
            @Parameter(description = "时间戳(毫秒)", required = true)
            @RequestHeader("X-Timestamp") Long timestamp,
            @Parameter(description = "随机数", required = true)
            @RequestHeader("X-Nonce") String nonce,
            @Parameter(description = "代理消息ID", required = true)
            @PathVariable String msgId) {
        
        try {
            // 1. 身份验证
            AppKeyAuthResponse authResponse = appAuthService.authenticateMsgCenter(
                appKey, signature, timestamp, nonce);
            if (!authResponse.isSuccess()) {
                return R.failed(authResponse.getErrorMsg());
            }
            
            // 2. 查询消息
            MsgAgentMapping mapping = msgAgentMappingService.getById(msgId);
            if (mapping == null) {
                return R.failed("消息不存在");
            }
            
            // 3. 权限验证
            if (!mapping.getAppKey().equals(appKey)) {
                return R.failed("无权访问此消息");
            }
            
            MsgAgentMappingVO vo = new MsgAgentMappingVO();
            BeanUtil.copyProperties(mapping, vo);
            
            return R.ok(vo, "查询成功");
            
        } catch (Exception e) {
            log.error("获取消息详情失败: msgId={}", msgId, e);
            return R.failed("查询失败: " + e.getMessage());
        }
    }
    
//    /**
//     * 获取轮询服务状态
//     */
//    @Operation(summary = "获取轮询服务状态", description = "获取消息轮询服务的状态信息")
//    @GetMapping("/polling/status")
//    public R<Map<String, Object>> getPollingStatus(
//            @Parameter(description = "应用系统编码", required = true)
//            @RequestHeader("X-App-Key") String appKey,
//            @Parameter(description = "签名信息", required = true)
//            @RequestHeader("X-Signature") String signature,
//            @Parameter(description = "时间戳(毫秒)", required = true)
//            @RequestHeader("X-Timestamp") Long timestamp,
//            @Parameter(description = "随机数", required = true)
//            @RequestHeader("X-Nonce") String nonce) {
//        
//        try {
//            // 1. 身份验证
//            AppKeyAuthResponse authResponse = appAuthService.authenticateMsgCenter(
//                appKey, signature, timestamp, nonce);
//            if (!authResponse.isSuccess()) {
//                return R.failed(authResponse.getErrorMsg());
//            }
//            
//            // 2. 获取轮询状态
//            Map<String, Object> status = messagePollingService.getPollingStatistics();
//            
//            return R.ok(status, "获取成功");
//            
//        } catch (Exception e) {
//            log.error("获取轮询状态失败", e);
//            return R.failed("获取失败: " + e.getMessage());
//        }
//    }
    
//    /**
//     * 手动触发轮询
//     */
//    @Operation(summary = "手动触发轮询", description = "手动触发消息轮询")
//    @PostMapping("/polling/trigger")
//    public R<Void> triggerPolling(
//            @Parameter(description = "应用系统编码", required = true)
//            @RequestHeader("X-App-Key") String appKey,
//            @Parameter(description = "签名信息", required = true)
//            @RequestHeader("X-Signature") String signature,
//            @Parameter(description = "时间戳(毫秒)", required = true)
//            @RequestHeader("X-Timestamp") Long timestamp,
//            @Parameter(description = "随机数", required = true)
//            @RequestHeader("X-Nonce") String nonce) {
//        
//        try {
//            // 1. 身份验证
//            AppKeyAuthResponse authResponse = appAuthService.authenticateMsgCenter(
//                appKey, signature, timestamp, nonce);
//            if (!authResponse.isSuccess()) {
//                return R.failed(authResponse.getErrorMsg());
//            }
//            
//            // 2. 触发轮询
//            boolean success = messagePollingService.triggerPolling(appKey);
//            
//            if (success) {
//                return R.ok(null, "触发轮询成功");
//            } else {
//                return R.failed("触发轮询失败");
//            }
//            
//        } catch (Exception e) {
//            log.error("触发轮询失败", e);
//            return R.failed("触发轮询失败: " + e.getMessage());
//        }
//    }
    
//    /**
//     * 获取应用统计信息
//     */
//    @Operation(summary = "获取应用统计信息", description = "获取应用的消息统计信息")
//    @GetMapping("/statistics")
//    public R<Map<String, Object>> getStatistics(
//            @Parameter(description = "应用系统编码", required = true)
//            @RequestHeader("X-App-Key") String appKey,
//            @Parameter(description = "签名信息", required = true)
//            @RequestHeader("X-Signature") String signature,
//            @Parameter(description = "时间戳(毫秒)", required = true)
//            @RequestHeader("X-Timestamp") Long timestamp,
//            @Parameter(description = "随机数", required = true)
//            @RequestHeader("X-Nonce") String nonce) {
//        
//        try {
//            // 1. 身份验证
//            AppKeyAuthResponse authResponse = appAuthService.authenticateMsgCenter(
//                appKey, signature, timestamp, nonce);
//            if (!authResponse.isSuccess()) {
//                return R.failed(authResponse.getErrorMsg());
//            }
//            
//            // 2. 获取统计信息
//            Map<String, Object> stats = new HashMap<>();
//            
//            // 消息总数
//            long totalCount = msgAgentMappingService.countByAppKey(appKey);
//            stats.put("totalCount", totalCount);
//            
//            // 未读消息数
//            long unreadCount = msgAgentMappingService.countUnreadByAppKey(appKey);
//            stats.put("unreadCount", unreadCount);
//            
//            // 今日消息数
//            Date today = DateUtil.beginOfDay(new Date());
//            MsgAgentMapping query = new MsgAgentMapping();
//            query.setAppKey(appKey);
//            query.setSendTime(today);
//            List<MsgAgentMapping> todayMessages = msgAgentMappingService.list(query);
//            stats.put("todayCount", todayMessages.size());
//            
//            // 按状态统计
//            Map<String, Long> statusStats = new HashMap<>();
//            String[] statuses = {"ACCEPTED", "SENDING", "CENTER_ACCEPTED", "COMPLETED", "FAILED"};
//            for (String status : statuses) {
//                query.setStatus(status);
//           		LambdaQueryWrapper<MsgAgentMapping> wrapper = new LambdaQueryWrapper<>();
//                wrapper.in(MsgAgentMapping::getStatus, (Object[])statuses);
//                long count = msgAgentMappingService.count(wrapper);
//                statusStats.put(status, count);
//            }
//            stats.put("statusStatistics", statusStats);
//            
//            return R.ok(stats, "获取成功");
//            
//        } catch (Exception e) {
//            log.error("获取统计信息失败", e);
//            return R.failed("获取失败: " + e.getMessage());
//        }
//    }
    
    // ==================== 私有工具方法 ====================
    
    /**
     * 构建消息编码申请请求
     */
    private CodeApplyRequest buildCodeApplyRequest(MsgSendRequestDTO requestDTO, 
                                                  MsgAppCredentialVO appCredential) {
        CodeApplyRequest request = new CodeApplyRequest();
        
        // 使用请求中的信息，如果为空则使用应用默认值
        request.setSqssdm(StrUtil.emptyToDefault(
            requestDTO.getSender().getSqssdm(), 
            appCredential.getDefaultSqssdm()));
        
        CodeApplyRequest.ApplyUnitInfo unitInfo = new CodeApplyRequest.ApplyUnitInfo();
        unitInfo.setSqdwdm(StrUtil.emptyToDefault(
            requestDTO.getSender().getSqdwxx().getSqdwdm(),
            appCredential.getDefaultSqdwmc()));
        unitInfo.setSqdwmc(StrUtil.emptyToDefault(
            requestDTO.getSender().getSqdwxx().getSqdwmc(),
            appCredential.getDefaultSqdwmc()));
        request.setSqdwxx(unitInfo);
        
        CodeApplyRequest.ApplyPersonInfo personInfo = new CodeApplyRequest.ApplyPersonInfo();
        personInfo.setSqrxm(StrUtil.emptyToDefault(
            requestDTO.getSender().getSqrxx().getSqrxm(),
            appCredential.getDefaultSqrxm()));
        personInfo.setSqrzjhm(StrUtil.emptyToDefault(
            requestDTO.getSender().getSqrxx().getSqrzjhm(),
            appCredential.getDefaultSqrzjhm()));
        personInfo.setSqrdh(StrUtil.emptyToDefault(
            requestDTO.getSender().getSqrxx().getSqrdh(),
            appCredential.getDefaultSqrdh()));
        request.setSqrxx(personInfo);
        
        return request;
    }
    
    /**
     * 构建部级消息发送请求
     */
    private List<MessageSendRequest> buildCenterMessageRequests(MsgSendRequestDTO requestDTO,
                                                               CodeApplyResponse codeResponse,
                                                               MsgAppCredentialVO appCredential) {
        List<MessageSendRequest> requests = new ArrayList<>();
        MessageSendRequest request = new MessageSendRequest();
        
        // 发送方信息
        request.setFsdw(requestDTO.getSender().getSqdwxx().getSqdwmc());
        request.setFsdwdm(requestDTO.getSender().getSqdwxx().getSqdwdm());
        request.setFsr(requestDTO.getSender().getSqrxx().getSqrxm());
        request.setFsrzjhm(requestDTO.getSender().getSqrxx().getSqrzjhm());
        
        // 发送对象
        request.setFsdx(requestDTO.getReceiver().getFsdx());
        
        // 接收方信息
        request.setJsdw(requestDTO.getReceiver().getJsdw());
        request.setJsdwdm(requestDTO.getReceiver().getJsdwdm());
        request.setJsr(requestDTO.getReceiver().getJsr());
        request.setJsrzjhm(requestDTO.getReceiver().getJsrzjhm());
        
        // 消息内容
        request.setXxbm(codeResponse.getJzptjcbm());
//        request.setXxlx(requestDTO.getMessage().getXxlx());
        request.setXxlx(msgSendQueueConfig.getMsgType());
        request.setXxbt(requestDTO.getMessage().getXxbt());
        request.setXxnr(requestDTO.getMessage().getXxnr());
        request.setCldz(requestDTO.getMessage().getCldz());
        request.setJjcd(requestDTO.getMessage().getJjcd());
        request.setYwcs(requestDTO.getMessage().getYwcs());
        request.setTb(requestDTO.getMessage().getTb());
        
        // 主题编码
        request.setZtbm(msgSendQueueConfig.getTopic());
        
        requests.add(request);
        return requests;
    }
    
    /**
     * 保存消息映射记录
     */
    private MsgAgentMapping saveMessageMapping(MsgSendRequestDTO requestDTO,
                                              MsgAppCredentialVO appCredential,
                                              CodeApplyResponse codeResponse) {
        MsgAgentMapping mapping = new MsgAgentMapping();
        mapping.setId(IdUtil.fastSimpleUUID());
        mapping.setAppKey(appCredential.getAppKey());
        mapping.setBizId(requestDTO.getBizId());
        mapping.setXxbm(codeResponse.getJzptjcbm());
        mapping.setCenterMsgId(codeResponse.getJzptjcbm());
        
        // 消息内容
        mapping.setMsgType(requestDTO.getMessage().getXxlx());
        mapping.setMsgTitle(requestDTO.getMessage().getXxbt());
        mapping.setPriority(3);
        mapping.setContent(requestDTO.getMessage().getXxnr());
        
        // 发送方信息
        mapping.setSenderName(requestDTO.getSender().getSqrxx().getSqrxm());
        mapping.setSenderIdcard(requestDTO.getSender().getSqrxx().getSqrzjhm());
        
        // 接收方信息
        mapping.setReceiverName(requestDTO.getReceiver().getJsr());
        mapping.setReceiverIdcard(requestDTO.getReceiver().getJsrzjhm());
        
        // 状态
        mapping.setStatus("ACCEPTED");
        mapping.setStatusCode("1000");
        mapping.setSendTime(new Date());
        
        // 回调配置
        mapping.setCallbackUrl(requestDTO.getCallbackConfig().getCallbackUrl());
        mapping.setCallbackMethod(requestDTO.getCallbackConfig().getMethod());
        mapping.setCallbackAuthMode(requestDTO.getCallbackConfig().getAuthMode());
        
        // 部级消息字段
        mapping.setCenterXxlx(requestDTO.getMessage().getXxlx());
        mapping.setCenterXxbt(requestDTO.getMessage().getXxbt());
        mapping.setCenterXxnr(requestDTO.getMessage().getXxnr());
        mapping.setCenterCldz(requestDTO.getMessage().getCldz());
        mapping.setCenterJjcd(requestDTO.getMessage().getJjcd());
        mapping.setCenterYwcs(requestDTO.getMessage().getYwcs());
        mapping.setCenterTb(requestDTO.getMessage().getTb());
        
        msgAgentMappingService.save(mapping);
        return mapping;
    }
    
    /**
     * 安排回调任务
     */
    private void scheduleCallbackTask(MsgAgentMapping mapping) {
        msgSendQueueService.createCallbackTask(
            mapping.getAppKey(),
            mapping.getId(),
            new Date()
        );
    }
    
    /**
     * 构建部级状态更新请求
     */
    private MessageStatusUpdateRequest buildCenterStatusUpdateRequest(
            MsgStatusUpdateRequestDTO requestDTO) {
        MessageStatusUpdateRequest request = new MessageStatusUpdateRequest();
        request.setCldw(requestDTO.getCldw());
        request.setCldwdm(requestDTO.getCldwdm());
        request.setClr(requestDTO.getClr());
        request.setClrzjhm(requestDTO.getClrzjhm());
        request.setXxbm(requestDTO.getXxbm());
        return request;
    }
    
    /**
     * 转换为发送响应VO
     */
    private MsgSendResponseVO convertToResponseVO(MsgAgentMapping mapping) {
        MsgSendResponseVO vo = new MsgSendResponseVO();
        vo.setMsgId(mapping.getId());
        vo.setBizId(mapping.getBizId());
        vo.setXxbm(mapping.getXxbm());
        vo.setSendTime(mapping.getSendTime());
        vo.setStatus(mapping.getStatus());
        vo.setStatusCode(mapping.getStatusCode());
        return vo;
    }
    
    /**
     * 转换为查询响应VO列表
     */
    private List<MsgQueryResponseVO> convertToQueryResponse(List<MsgAgentMapping> messages) {
        return messages.stream().map(mapping -> {
            MsgQueryResponseVO vo = new MsgQueryResponseVO();
            
            // 发送方信息
            vo.setFsdw(mapping.getCenterFsdw());
            vo.setFsdwdm(mapping.getCenterFsdwdm());
            vo.setFsr(mapping.getCenterFsr());
            vo.setFsrzjhm(mapping.getCenterFsrzjhm());
            vo.setFsdx(mapping.getCenterFsdx());
            
            // 接收方信息
            vo.setJsdw(mapping.getCenterJsdw());
            vo.setJsdwdm(mapping.getCenterJsdwdm());
            vo.setJsr(mapping.getCenterJsr());
            vo.setJsrzjhm(mapping.getCenterJsrzjhm());
            
            // 消息内容
            vo.setXxbm(mapping.getXxbm());
            vo.setXxlx(mapping.getCenterXxlx());
            vo.setXxbt(mapping.getCenterXxbt());
            vo.setXxnr(mapping.getCenterXxnr());
            vo.setCldz(mapping.getCenterCldz());
            vo.setJjcd(mapping.getCenterJjcd());
            vo.setYwcs(mapping.getCenterYwcs());
            vo.setTb(mapping.getCenterTb());
            
            vo.setClzt(mapping.getCenterClzt());
            
            return vo;
        }).collect(Collectors.toList());
    }
    
    /**
     * VO转Entity
     */
    private MsgAppCredential convertToEntity(MsgAppCredentialVO vo) {
        MsgAppCredential entity = new MsgAppCredential();
        BeanUtil.copyProperties(vo, entity);
        return entity;
    }
}