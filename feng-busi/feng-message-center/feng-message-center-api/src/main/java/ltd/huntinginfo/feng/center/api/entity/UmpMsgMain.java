package ltd.huntinginfo.feng.center.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 消息主表实体类
 * 对应表：ump_msg_main
 * 作用：存储所有消息的核心元数据，支持消息状态流转和统计
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "ump_msg_main", autoResultMap = true)
@Schema(description = "消息主表实体")
public class UmpMsgMain implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "消息ID(UUID)", example = "msg_1234567890abcdef")
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @Schema(description = "消息编码(xxbm)", example = "XXBM-DE85WY5M2Y6")
    @TableField("msg_code")
    private String msgCode;

    @Schema(description = "消息类型", example = "NOTICE", allowableValues = {"NOTICE", "ALERT", "BIZ", "AGENT"})
    @TableField("msg_type")
    private String msgType;

    @Schema(description = "消息标题", example = "系统维护通知")
    @TableField("title")
    private String title;

    @Schema(description = "消息内容(JSON格式)", example = "{\r\n"
    		+ "      \"header\": {\r\n"
    		+ "        \"title\": \"关于2026年度预算申报的通知\",\r\n"
    		+ "        \"sub_title\": \"财务处〔2026〕12号\"\r\n"
    		+ "      },\r\n"
    		+ "      \"body\": [\r\n"
    		+ "        \"各单位：\",\r\n"
    		+ "        \"为做好2026年度预算编制工作，现将有关事项通知如下。\",\r\n"
    		+ "        \"一、预算申报截止时间为2026年3月31日。\",\r\n"
    		+ "        \"二、请通过财政一体化平台完成申报。\",\r\n"
    		+ "        \"特此通知。\"\r\n"
    		+ "      ],\r\n"
    		+ "      \"footer\": {\r\n"
    		+ "        \"org\": \"财政处\",\r\n"
    		+ "        \"date\": \"2026-01-10\"\r\n"
    		+ "      }\r\n"
    		+ "    }")
    @TableField(value = "content")
    private String content;

    @Schema(description = "优先级(1-5,数字越小优先级越高)", example = "3")
    @TableField("priority")
    private Integer priority;

    @Schema(description = "发送应用标识", example = "APP001")
    @TableField("sender_app_key")
    private String senderAppKey;

    @Schema(description = "发送者类型", example = "APP", allowableValues = {"APP", "USER", "SYSTEM"})
    @TableField("sender_type")
    private String senderType;

    @Schema(description = "发送者ID", example = "user_001")
    @TableField("sender_id")
    private String senderId;

    @Schema(description = "发送者名称", example = "系统管理员")
    @TableField("sender_name")
    private String senderName;

    @Schema(description = "发送单位代码", example = "130100000000")
    @TableField("sender_org_code")
    private String senderOrgCode;

    @Schema(description = "发送单位名称", example = "石家庄市消防局")
    @TableField("sender_org_name")
    private String senderOrgName;

    @Schema(description = "代理消息ID", example = "agent_msg_123")
    @TableField("agent_msg_id")
    private String agentMsgId;

    @Schema(description = "代理平台标识", example = "AGENT_PLATFORM")
    @TableField("agent_app_key")
    private String agentAppKey;

    @Schema(description = "接收者数量", example = "100")
    @TableField("receiver_count")
    private Integer receiverCount;

    @Schema(description = "接收者类型", example = "USER", allowableValues = {"USER", "DEPT", "ORG", "AREA", "ALL"})
    @TableField("receiver_type")
    private String receiverType;

    @Schema(description = "接收者范围配置(JSON)", example = "{\r\n"
    		+ "      \"include\": {\r\n"
    		+ "        \"appKeys\": [\"app001\", \"app002\"],\r\n"
    		+ "        \"loginIds\": [\"u001\", \"u002\"],\r\n"
    		+ "        \"deptIds\": [\"D_FIN_001\"],\r\n"
    		+ "        \"agencyCodes\": [\"AG_1001\"],\r\n"
    		+ "        \"orgCodes\": [\"ORG_1001\"],\r\n"
    		+ "        \"divisionCodes\": [\"110000\"],\r\n"
    		+ "        \"roleCodes\": [\"FIN_MANAGER\"]\r\n"
    		+ "      },\r\n"
    		+ "      \"exclude\": {\r\n"
    		+ "        \"loginIds\": [\"u003\", \"u004\"],\r\n"
    		+ "        \"deptIds\": [\"D_FIN_002\"],\r\n"
    		+ "        \"roleCodes\": [\"DEV_MANAGER\"]\r\n"
    		+ "      }\r\n"
    		+ "    }")
    @TableField(value = "receiver_scope")
    private String receiverScope;

    @Schema(description = "回调地址", example = "http://callback.example.com/api/message/status")
    @TableField("callback_url")
    private String callbackUrl;

    @Schema(description = "推送方式", example = "PUSH", allowableValues = {"PUSH", "POLL"})
    @TableField("push_mode")
    private String pushMode;

    @Schema(description = "回调配置(JSON)", example = "{\r\n"
    		+ "  \"method\": \"POST\",\r\n"
    		+ "  \"timeout_ms\": 3000,\r\n"
    		+ "\r\n"
    		+ "  \"headers\": {\r\n"
    		+ "    \"X-App-Key\": \"${sender_app_key}\",\r\n"
    		+ "    \"X-Request-Id\": \"${msg_id}\"\r\n"
    		+ "  },\r\n"
    		+ "\r\n"
    		+ "  \"body\": {\r\n"
    		+ "    \"msg_id\": \"${msg_id}\",\r\n"
    		+ "    \"msg_code\": \"${msg_code}\",\r\n"
    		+ "    \"status\": \"${status}\"\r\n"
    		+ "  },\r\n"
    		+ "\r\n"
    		+ "  \"success_condition\": {\r\n"
    		+ "    \"http_status\": 200,\r\n"
    		+ "    \"resp_code\": \"0\"\r\n"
    		+ "  }\r\n"
    		+ "}")
    @TableField(value = "callback_config")
    private String callbackConfig;

    @Schema(description = "扩展参数(JSON)", example = "{\r\n"
    		+ "  \"trace\": {\r\n"
    		+ "    \"trace_id\": \"bgt-20260110-001\"\r\n"
    		+ "  },\r\n"
    		+ "  \"flags\": {\r\n"
    		+ "    \"test\": false\r\n"
    		+ "  },\r\n"
    		+ "  \"kv\": {\r\n"
    		+ "    \"env\": \"prod\",\r\n"
    		+ "    \"caller\": \"budget-system\"\r\n"
    		+ "  }\r\n"
    		+ "}")
    @TableField(value = "ext_params")
    private String extParams;

    @Schema(description = "过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("expire_time")
    private LocalDateTime expireTime;

    @Schema(description = "消息状态", example = "RECEIVED", 
            allowableValues = {"RECEIVED", "DISTRIBUTING", "DISTRIBUTED", "DIST_FAILED", "PUSHED", "PUSH_FAILED", 
            		"BIZ_RECEIVED", "POLL", "BIZ_PULLED", "POLL_FAILED", "READ"})
    @TableField("status")
    private String status;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "发送时间（发送到队列）")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("send_time")
    private LocalDateTime sendTime;

    @Schema(description = "分发时间（分发到收件箱或者广播信息筒）")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("distribute_time")
    private LocalDateTime distributeTime;

    @Schema(description = "完成时间（消息生命周期终结，例如已读")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("complete_time")
    private LocalDateTime completeTime;

    @Schema(description = "总接收人数", example = "100")
    @TableField("total_receivers")
    private Integer totalReceivers;

    @Schema(description = "已接收人数", example = "50")
    @TableField("received_count")
    private Integer receivedCount;

    @Schema(description = "已读人数", example = "30")
    @TableField("read_count")
    private Integer readCount;

    @Schema(description = "逻辑删除标记(0-正常 1-删除)", example = "0")
    @TableField("del_flag")
    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}