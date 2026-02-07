package ltd.huntinginfo.feng.ai.utils;

import dev.langchain4j.model.input.Prompt;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import ltd.huntinginfo.feng.common.core.util.StreamEmitter;

import java.util.concurrent.Executor;

/**
 * AI流发射器
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Data
@Accessors(chain = true)
public class AiEmitter {
    @Schema(description = "提示词对象")
    private Prompt prompt;
    
    @Schema(description = "流对象")
    private StreamEmitter emitter;
    
    @Schema(description = "执行器对象")
    private Executor executor;
}
