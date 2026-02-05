package work.metanet.feng.ai.api.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import work.metanet.feng.common.core.constant.enums.DocType;
import work.metanet.feng.common.core.constant.enums.SliceStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;

/**
 * 知识库之文档实体类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Data
@Accessors(chain = true)
@Schema(name = "生成式AI文档",description = "文档表")
public class AigcDocs implements Serializable {
    private static final long serialVersionUID = 548724967827903685L;

    /**
     * 主键
     */
    @Schema(description = "文档ID")
    private Integer id;

    /**
     * 知识库ID
     */
    @Schema(description = "知识ID")
    private Integer knowledgeId;
    
    /**
     * 文件ID
     */
    @Schema(description = "文件ID")
    private Integer ossId;

    /**
     * 名称
     */
    @Schema(description = "名称")
    private String name;

    /**
     * 类型
     */
    @Schema(description = "类型")
    private String type;

    /**
     * 来源
     */
    @Schema(description = "来源")
    private String origin;

    /**
     * 文档内容
     */
    @Schema(description = "文档内容")
    private String content;

    /**
     * 文档中第一个文件的链接
     */
    @Schema(description = "链接")
    @TableField(exist = false)
    private String url;
    
    @TableField(exist = false)
    private List<AigcOss> ossList;

    /**
     * 文件大小
     */
    @Schema(description = "文件大小")
    private Long size;

    /**
     * 切片数量
     */
    @Schema(description = "切片数量")
    private Integer sliceNum;
    
    @Schema(description = "切片最大长度")
    private Integer maxLength;
    
    @Schema(description = "切片交叉覆盖大小")
    private Integer overlapSize;
    
    @Schema(description = "切片模式")
    private String sliceMode;
    
    /**
     * 最后切片时间
     */
	@Schema(description = "最后切片时间")
	private LocalDateTime lastSliceTime;

    /**
     * 切片状态
     */
	@Schema(description = "切片状态")
    private Integer sliceStatus;

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

