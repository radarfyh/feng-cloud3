package work.metanet.feng.ai.api.dto;

import dev.langchain4j.model.input.Prompt;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 图片请求DTO类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */
@Data
@Accessors(chain = true)
@Schema(name = "图片请求",description = "图片请求DTO")
public class ImageR {
	@Schema(description = "模型ID")
    private Integer modelId;
	@Schema(description = "模型名称")
    private String modelName;
	@Schema(description = "模型供应商")
    private String modelProvider;
	@Schema(description = "提示词")
    private Prompt prompt;

    /**
     * 内容
     */
	@Schema(description = "消息内容")
    private String message;

    /**
     * 质量
     */
	@Schema(description = "质量")
    private String quality;

    /**
     * 尺寸
     */
	@Schema(description = "大小")
    private String size;

    /**
     * 风格
     */
	@Schema(description = "风格")
    private String style;
}
