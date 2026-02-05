package work.metanet.feng.ai.api.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import work.metanet.feng.ai.api.dto.OssR;

/**
 * 文件资源实体类
 * 文件资源和文档 之间的关系：1:1，特殊情况下是n:1
 * 
 * @author EdisonFeng
 * @since 2025/5/9
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Data
@Accessors(chain = true)
@Schema(name = "生成式AI对象存储",description = "对象存储表")
public class AigcOss implements Serializable {
    private static final long serialVersionUID = -250127374910520163L;

    /**
     * 主键
     */
    @Schema(description = "资源ID")
    private Integer id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Integer userId;

    /**
     * 文档ID
     */
    @Schema(description = "文档ID")
    private Integer docId;
    
    /**
     * 文件提取内容
     */
    @Schema(description = "文件提取内容，前端空着，后台自动处理")
    private String extractContent;

    @Schema(description = "对象存储ID for 对象存储系统，若无则空")
    private String ossId;
    
    @Schema(description = "知识ID")
    private Integer knowledgeId;
    
    @Schema(description = "文件ID for 操作系统")
    private String fileId;
    
    @Schema(description = "链接", requiredMode = RequiredMode.REQUIRED)
    private String url;
    
    @Schema(description = "大小")
    private Long size;
    
    @Schema(description = "文件名称")
    private String filename;
    
    @Schema(description = "原始文件名称", requiredMode = RequiredMode.REQUIRED)
    private String originalFilename;
    
    @Schema(description = "基准路径")
    private String basePath;
    
    @Schema(description = "路径")
    private String path;
    
    @Schema(description = "扩展")
    private String ext;
    
    @Schema(description = "内容类型")
    private String contentType;
    
    @Schema(description = "平台")
    private String platform;
    
    @Schema(description = "预拆分最大长度")
    private Integer maxLength;
    
    @Schema(description = "预拆分交叉覆盖大小")
    private Integer overlapSize;
    
    @Schema(description = "预拆分模式，保留")
    private String sliceMode;
    
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
