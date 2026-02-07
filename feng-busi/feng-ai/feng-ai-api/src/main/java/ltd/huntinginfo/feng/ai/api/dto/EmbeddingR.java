package ltd.huntinginfo.feng.ai.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 向量请求DTO类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */
@Data
@Accessors(chain = true)
@Schema(name = "向量请求",description = "向量请求DTO")
public class EmbeddingR {

    /**
     * 写入到vector store的ID
     */
	@Schema(description = "向量库管理表ID，aigc_embed_store表的id")
    private Integer EmbedStoreId;
	
    /**
     * 写入到vector store的ID（PGVector中embedding_id字段
     */
	@Schema(description = "向量ID，PGVector中embedding_id字段")
    private String vectorId;

    /**
     * 文档ID
     */
	@Schema(description = "文档ID")
    private Integer docsId;

    /**
     * 知识库ID
     */
	@Schema(description = "知识ID")
    private Integer knowledgeId;

    /**
     * Embedding后切片的文本
     */
	@Schema(description = "向量切片文本")
    private String text;
}
