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
 * 和LLM服务官方API建立会话时提供的状态和信息实体类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Data
@Accessors(chain = true)
@Schema(name = "生成式AI会话",description = "会话表")
public class AigcConversation implements Serializable {

    private static final long serialVersionUID = -19545329638997333L;

    /**
     * 会话ID
     */
    @Schema(description = "会话ID，主键")
    private Integer id;

    /**
     * 渠道ID
     */
    @Schema(description = "渠道ID")
    private Integer appApiId;
    
    /**
     * 知识库ID
     */
    @Schema(description = "知识库ID")
    private Integer knowledgeId;
    
    /**
     * 提示词
     */
    @Schema(description = "提示语")
    @TableField(exist = false)
    private String prompt;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Integer userId;

    /**
     * 会话标题
     */
    @Schema(description = "标题")
    private String title;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    @TableField(exist = false)
    private String username;

    /**
     * 对话次数
     */
    @Schema(description = "会话次数")
    private Integer chatTotal;
    /**
     * Token消耗量
     */
    @Schema(description = "令牌消耗量")
    private Integer tokenUsed;
    /**
     * 最后一次对话时间
     */
    @Schema(description = "最后一次会话时间")
    private LocalDateTime endTime;
    
    /**
     * 状态
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

