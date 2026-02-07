package ltd.huntinginfo.feng.ai.api.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI消息实体类，支持系统、用户、助手消息类型
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Data
@Accessors(chain = true)
@Schema(name = "生成式AI消息",description = "消息表")
public class AigcMessage implements Serializable {

    private static final long serialVersionUID = -19545329638997333L;

    /**
     * 主键
     */
    @Schema(description = "消息ID")
    private Integer id;
    
    /**
     * 会话ID
     */
    @Schema(description = "会话ID")
    private Integer conversationId;
    
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Integer userId;

    /**
     * 上级ID
     */
    @Schema(description = "上级ID")
    private Integer parentMessageId;
    
    /**
     * 子对话ID
     */
    @Schema(description = "子对话ID，UUID")
    private String chatId;
    
    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * 请求IP
     */
    @Schema(description = "请求IP")
    private String ip;

    /**
     * 令牌数
     */
    @Schema(description = "令牌数")
    private Integer tokens;
    
    /**
     * 提示词令牌数
     */
    @Schema(description = "提示词令牌数")
    private Integer promptTokens;

    /**
     * 角色，user、assistant、system
     */
    @Schema(description = "角色：user、assistant、system")
    private String role;

    /**
     * 模型名称
     */
    @Schema(description = "模型名称")
    private String model;

    /**
     * 消息内容
     */
    @Schema(description = "消息内容")
    private String message;

    /**
     * 是否最后回复
     */
    @Schema(description = "是否最后回复")
    private Boolean isFinal;

    /**
     * 状态，当回复异常时，填1
     */
    @Schema(description = "状态 1启用 0禁用")
    private String status;
    /**
     * 创建者
     */
    @Schema(description = "创建者")
    private String createBy;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /**
     * 更新者
     */
    @Schema(description = "更新者")
    private String updateBy;
    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
    /**
     * 逻辑删
     */
    @Schema(description = "逻辑删 0-未删除 1-已删除")
    @TableLogic
    private String delFlag;

}

