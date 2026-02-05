package work.metanet.feng.ai.api.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import work.metanet.feng.common.core.constant.enums.SliceMode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 知识库实体类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Data
@TableName(autoResultMap = true)
@Schema(name = "生成式AI知识",description = "知识库表")
public class AigcKnowledge implements Serializable {
    private static final long serialVersionUID = 548724967827903685L;

    /**
     * 主键，知识ID
     */
    @Schema(description = "知识ID")
    private Integer id;
    
    /**
     * 应用ID
     */
    @Schema(description = "应用ID")
    private Integer appId;
    
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Integer userId;
    
    /**
     * 向量库ID
     */
    @Schema(description = "向量库ID")
    private Integer embedStoreId;
    
    /**
     * 向量模型ID
     */
    @Schema(description = "向量模型ID")
    private Integer embedModelId;

    /**
     * 知识库名称
     */
    @Schema(description = "知识库名称")
    private String name;

    /**
     * 封面
     */
    @Schema(description = "封面")
    private String cover;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String des;
    
    @Schema(description = "切片最大长度")
    private Integer maxLength;
    
    @Schema(description = "切片交叉覆盖大小")
    private Integer overlapSize;
    
    @Schema(description = "切片模式")
    private String sliceMode;
    
    /**
     * 文档数量
     */
    @Schema(description = "文档数量")
    @TableField(exist = false)
    private Integer docsNum = 0;
    
    /**
     * 总大小
     */
    @Schema(description = "总大小")
    @TableField(exist = false)
    private Long totalSize = 0L;
    
    /**
     * 文档对象的列表
     */
    @TableField(exist = false)
    private List<AigcDocs> docs = new ArrayList<>();

    /**
     * 向量库
     */
    @TableField(exist = false)
    private AigcEmbedStore embedStore;
    
    /**
     * 会话对象的列表
     */
    @TableField(exist = false)
    private List<AigcConversation> conversations;
    
    /**
     * 向量模型
     */
    @TableField(exist = false)
    private AigcModel embedModel;
    
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
    
    @Schema(description = "切片策略配置")
    @TableField(exist = false)
    private SliceConfig sliceConfig;

    @Data
    @AllArgsConstructor
    public static class SliceConfig {
        public static final Integer MAX_SLICE_SIZE = 1000;
        public static final Integer OVERLAP_SIZE = 100;

        private Integer maxSliceSize = MAX_SLICE_SIZE;    // 单切片最大字符数
        private Integer overlapSize = OVERLAP_SIZE;      // 切片重叠字符数
        private String sliceMode = SliceMode.FIXED.getCode(); // paragraph/sentence/fixed
    }
}

