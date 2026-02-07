package ltd.huntinginfo.feng.ai.api.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 提示语实体类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Data
@TableName("aigc_prompt")
public class AigcPrompt {
	
	/**
	 * 提示语ID
	 */
	@Schema(description = "提示语ID")
    @TableId(type = IdType.AUTO)
    private Integer id;
	
	@Schema(description = "提示语名称")
    private String name;
	
	@Schema(description = "提示语内容")
    private String content;
	
	@Schema(description = "提示语所属渠道ID")
    private Integer appApiId;
    
	/**
	 * system	系统级提示，定义 AI 的身份、行为和整体风格	"你是一个专业医生，请以严谨口吻回答所有问题。"	全局生效，一般在对话开始前设置一次
	 * instruction	指令式提示，引导 AI 完成特定任务	"请帮我总结下面这篇文章的要点。"	一次性使用，通常由用户或者流程动态下达
	 * template	模板化提示，预留占位符，根据实际需要动态填充内容	"请用中文总结这段内容：{{content}}"	可重复复用，通过变量灵活生成不同场景提示
	 */
	@Schema(description = "提示词类型（system, instruction, template 等）")
    private String type;
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