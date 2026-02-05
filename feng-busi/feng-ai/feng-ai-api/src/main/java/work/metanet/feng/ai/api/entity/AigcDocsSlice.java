package work.metanet.feng.ai.api.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;

/**
 * 文档之切片实体类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Data
@Accessors(chain = true)
@Schema(name = "生成式AI文档切片",description = "文档切片表")
public class AigcDocsSlice implements Serializable {
    private static final long serialVersionUID = -3093489071059867065L;

    /**
     * 主键，文档切片ID
     */
    @Schema(description = "文档切片ID")
    private Integer id;

    /**
     * 向量库ID，关联aigc_embed_store
     */
    @Schema(description = "向量库ID，关联aigc_embed_store")
    private Integer embedStoreId;

    /**
     * 向量ID，关联pgvector向量表（例如vector_items）的embedding_id
     */
    @Schema(description = "向量ID，关联pgvector向量表（例如vector_items）的embedding_id")
    private String vectorId;
    /**
     * 文档ID
     */
    @Schema(description = "文档ID")
    private Integer docsId;
    
    /**
     * 文件ID
     */
    @Schema(description = "文件ID")
    private Integer ossId;

    /**
     * 知识库ID
     */
    @Schema(description = "知识ID")
    private Integer knowledgeId;

    /**
     * 文档名称
     */
    @Schema(description = "名称")
    private String name;

    /**
     *  记录当前切片在文档中的顺序位置
     */
    @Schema(description = "切片序号")
    private Integer sliceIndex;  

    /**
     *  用于内容去重校验
     */
    @Schema(description = "切片哈希值")
    private String contentHash;  
    
    /**
     *  存储自动提取的关键词
     */
    @Schema(description = "关键词提取")
    private String keywords;     

    /**
     *  切片内容的摘要
     */
    @Schema(description = "摘要")
    private String summary;      
    
    /**
     * 切片内容
     */
    @Schema(description = "切片内容")
    private String content;

    /**
     * 字符数量
     */
    @Schema(description = "字符数量")
    private Integer wordNum;

    /**
     * 是否Embedding
     */
    @Schema(description = "是否向量模型")
    private Boolean isEmbedding = false;

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

