package ltd.huntinginfo.feng.ai.api.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import ltd.huntinginfo.feng.common.core.constant.enums.ModelTypeEnum;
import ltd.huntinginfo.feng.common.core.constant.enums.ProviderEnum;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;

/**
 * 模型实体类
 * 关键信息是API Key，有的模型需要额外配置密钥
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Data
@Accessors(chain = true)
@Schema(name = "生成式AI模型",description = "模型表")
public class AigcModel implements Serializable {
    private static final long serialVersionUID = -19545329638997333L;

    /**
     * 模型 ID
     */
    @Schema(description = "模型ID")
    private Integer id;

    /**
     * 类型
     */
    @Schema(description = "类型:chat/embedding/image")
    private String type;
    
    /**
     * 模型技术术语名称
     */
    @Schema(description = "对应的模型技术术语名称")
    private String model;
    
    @Schema(description = "供应商")
    @TableField("provider")
    private String provider;
    
    @Schema(description = "名称")
    private String name;
    
    @Schema(description = "响应长度【1，8192】")
    private Integer responseLimit;
    
    @Schema(description = "温度【0，2】")
    private Double temperature = 0.2;
    
    @Schema(description = "候选词百分比【0，1】")
    private Double topP = 0.0;
    
    @Schema(description = "API KEY")
    private String apiKey;
    
    @Schema(description = "模型密钥（Secret Key），有些模型需要，例如百度")
    private String secretKey;
    
    @Schema(description = "模型基准链接（base url）")
    private String baseUrl;
    
    @Schema(description = "模型端点（end point）")
    private String endpoint;
    
    @Schema(description = "gemini服务器位置")
    private String geminiLocation;
    @Schema(description = "gemini项目名称")
    private String geminiProject;
    @Schema(description = "azure部署名称")
    private String azureDeploymentName;

    /**
     * 图片大小
     */
    @Schema(description = "图片大小")
    private String imageSize;
    @Schema(description = "图片质量")
    private String imageQuality;
    @Schema(description = "图片风格")
    private String imageStyle;
    
    /**
     * 向量维度
     */
    @Schema(description = "向量维度")
    private Integer dimension;
    
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

