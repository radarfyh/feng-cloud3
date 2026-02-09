package ltd.huntinginfo.feng.center.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName(value = "msg_agent_mapping", autoResultMap = true)
@Schema(name = "消息映射", description = "消息映射实体，记录代理平台与消息中心的消息映射关系")
public class MsgAgentMapping implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    @Schema(description = "唯一标识UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(description = "应用标识，即业务系统标识", requiredMode = Schema.RequiredMode.REQUIRED)
    private String appKey;
    
    @Schema(description = "业务ID，对于某个APPKEY，不能重复否则判定重复请求", requiredMode = Schema.RequiredMode.REQUIRED)
    private String bizId;

    // ========== 部级消息标识 ==========
    @Schema(description = "部级消息编码")
    private String xxbm;
    
    @Schema(description = "部级消息ID")
    private String centerMsgId;

    // ========== 消息内容摘要 ==========
    @Schema(description = "消息类型")
    private String msgType;
    
    @Schema(description = "消息标题")
    private String msgTitle;
    
    @Schema(description = "优先级1-5", defaultValue = "3")
    private Integer priority;
    
    @Schema(description = "消息内容")
    private String content;

    // ========== 代理平台发送方信息 ==========
    @Schema(description = "发送者单位代码")
    private String senderOrgCode;
    
    @Schema(description = "发送者单位名称")
    private String senderOrgName;
    
    @Schema(description = "发送者证件号码")
    private String senderIdcard;
    
    @Schema(description = "发送者姓名")
    private String senderName;

    // ========== 代理平台接收方信息 ==========
    @Schema(description = "接收者类型 USER/ROLE/DEPT/ORG")
    private String receiverType;
    
    @Schema(description = "接收者单位代码")
    private String receiverOrgCode;
    
    @Schema(description = "接收者单位名称")
    private String receiverOrgName;
    
    @Schema(description = "接收者证件号码")
    private String receiverIdcard;
    
    @Schema(description = "接收者姓名")
    private String receiverName;

    // ========== 状态追踪 ==========
    @Schema(description = "消息状态", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;
    
    @Schema(description = "状态码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String statusCode;
    
    @Schema(description = "状态详情")
    private String statusDetail;

    // ========== 时间戳 ==========
    @Schema(description = "发送时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private Date sendTime;
    
    @Schema(description = "部级接收时间")
    private Date centerReceiveTime;
    
    @Schema(description = "部级处理时间")
    private Date centerProcessTime;
    
    @Schema(description = "回调业务系统时间")
    private Date callbackTime;
    
    @Schema(description = "业务系统确认时间")
    private Date bizAckTime;
    
    @Schema(description = "完成时间")
    private Date completeTime;

    // ========== 重试信息 ==========
    @Schema(description = "重试次数", defaultValue = "0")
    private Integer retryCount;
    
    @Schema(description = "最大重试次数", defaultValue = "3")
    private Integer maxRetryCount;
    
    @Schema(description = "下次重试时间")
    private Date nextRetryTime;

    // ========== 回调信息 ==========
    @Schema(description = "回调地址")
    private String callbackUrl;
    
    @Schema(description = "回调方法", defaultValue = "POST")
    private String callbackMethod;
    
    @Schema(description = "回调认证模式", defaultValue = "standard")
    private String callbackAuthMode;
    
    @Schema(description = "回调签名")
    private String callbackSignature;
    
    @Schema(description = "回调时间戳(ms)")
    private Long callbackTimestamp;
    
    @Schema(description = "回调随机字符串")
    private String callbackNonce;

    // ========== 扩展信息 ==========
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    @Schema(description = "扩展参数")
    private Object extParams;

    // ========== 消息中心消息相关字段 ==========
    @Schema(description = "消息中心消息类型")
    private String centerXxlx;
    
    @Schema(description = "消息中心消息标题")
    private String centerXxbt;
    
    @Schema(description = "消息中心消息内容")
    private String centerXxnr;
    
    @Schema(description = "消息中心处理地址")
    private String centerCldz;
    
    @Schema(description = "消息中心紧急程度")
    private String centerJjcd;
    
    @Schema(description = "消息中心业务参数")
    private String centerYwcs;
    
    @Schema(description = "消息中心图标(base64)")
    private String centerTb;

    // ========== 消息中心发送方信息 ==========
    @Schema(description = "消息中心发送单位")
    private String centerFsdw;
    
    @Schema(description = "消息中心发送单位代码")
    private String centerFsdwdm;
    
    @Schema(description = "消息中心发送人")
    private String centerFsr;
    
    @Schema(description = "消息中心发送人证件号码")
    private String centerFsrzjhm;
    
    @Schema(description = "消息中心发送对象")
    private String centerFsdx;
    
    @Schema(description = "消息中心发送时间")
    private Date centerFssj;

    // ========== 消息中心接收方信息 ==========
    @Schema(description = "消息中心接收单位")
    private String centerJsdw;
    
    @Schema(description = "消息中心接收单位代码")
    private String centerJsdwdm;
    
    @Schema(description = "消息中心接收人")
    private String centerJsr;
    
    @Schema(description = "消息中心接收人证件号码")
    private String centerJsrzjhm;
    
    @Schema(description = "消息中心处理状态 0-未读 1-已读", defaultValue = "0")
    private String centerClzt;

    // ========== 审计字段 ==========
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createTime;

    @TableField(fill = FieldFill.UPDATE)
    @Schema(description = "修改时间")
    private Date updateTime;
    
    @TableLogic
    @Schema(description = "逻辑删 0-正常 1-删除", defaultValue = "0")
    private String delFlag;

    // ========== 业务逻辑方法 ==========
    
    /**
     * 判断消息是否已完成
     */
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }
    
    /**
     * 判断消息是否已读
     */
    public boolean isRead() {
        return "1".equals(centerClzt);
    }
    
    /**
     * 判断是否可以重试
     */
    public boolean canRetry() {
        return retryCount < maxRetryCount && 
               (nextRetryTime == null || new Date().after(nextRetryTime));
    }
    
    /**
     * 更新状态并记录详情
     */
    public void updateStatus(String newStatus, String newStatusCode, String detail) {
        this.status = newStatus;
        this.statusCode = newStatusCode;
        this.statusDetail = detail;
        this.updateTime = new Date();
        
        // 根据状态更新时间字段
        if ("CALLBACK_SENT".equals(newStatus)) {
            this.callbackTime = new Date();
        } else if ("CALLBACK_ACKED".equals(newStatus)) {
            this.bizAckTime = new Date();
        } else if ("COMPLETED".equals(newStatus)) {
            this.completeTime = new Date();
        }
    }
}