package work.metanet.feng.ai.api.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 渠道实体类
 * 渠道本来只LLM服务官方API的代理，本项目转为应用程序的一种分类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Data
@Accessors(chain = true)
@Schema(name = "生成式应用渠道",description = "生成式应用渠道表")
public class AigcAppApi implements Serializable {
    private static final long serialVersionUID = -94917153262781949L;

    /**
     * 主键，渠道ID
     */
    @Schema(description = "渠道ID")
    private Integer id;
    
    /**
     * 应用ID
     */
    @Schema(description = "应用ID")
    private Integer appId;
    
    /**
     * 渠道的api KEY
     */
    @Schema(description = "API KEY，渠道自有，默认拷贝自应用所属模型API KEY")
    private String apiKey;
    
    @Schema(description = "渠道密钥（Secret Key），渠道自有，默认拷贝自应用所属模型")
    private String secretKey;
    
    @Schema(description = "渠道基准链接（base url），渠道自有，默认拷贝自应用所属模型")
    private String baseUrl;  
    
    /**
     * 应用渠道名称
     */
    @Schema(description = "应用渠道名称")
    private String channel;
    
    /**
     * 应用对象
     */
    @TableField(exist = false)
    private AigcApp app;

    /**
     * 提示语对象的列表
     */
    @TableField(exist = false)
    private List<AigcPrompt> prompts;
    
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
