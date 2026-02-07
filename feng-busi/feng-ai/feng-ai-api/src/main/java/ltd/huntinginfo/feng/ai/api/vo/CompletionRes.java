package ltd.huntinginfo.feng.ai.api.vo;

import dev.langchain4j.model.output.Response;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * 完成应答VO类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Data
@Builder
@Schema(name = "完成应答",description = "完成应答VO")
public class CompletionRes {
	@Schema(description = "应答ID")
    private final Integer id;
	@Schema(description = "创建时间，Unix时间戳（以秒为单位）")
    private final Integer created;
	@Schema(description = "模型名称")
    private final String model;
	@Schema(description = "choices列表，元素为聊天完成选择对象，包含choice索引、模型生成的消息（对话角色、对话消息内容）、模型停止生成标记的原因（stop: 模型生成遇到自然停止点或提供的停止序列；length: 达到请求中指定的最大标记数；content_filter：如果由于内容过滤器中的标志而省略了内容；tool_calls/function_call： 模型调用了函数）")
    private final List<ChatCompletionChoice> choices;
	@Schema(description = "请求使用情况的统计信息（生成token数，输入token数，使用的token总数（prompt + completion））")
    private final Usage usage;

    public static CompletionRes process(String token) {
    	List<ChatCompletionChoice> list = Arrays.asList(ChatCompletionChoice
                .builder()
                .delta(Delta.builder().content(token).build())
                .build());
        return CompletionRes.builder()
                .choices(list)
                .build();
    }

    public static CompletionRes end(Response res) {
    	List<ChatCompletionChoice> list = Arrays.asList(ChatCompletionChoice
                .builder()
                .finishReason(res.finishReason() == null ? "finish" : res.finishReason().toString())
                .build());
    	
        return CompletionRes.builder()
                .usage(Usage.builder()
                        .completionTokens(res.tokenUsage().outputTokenCount())
                        .promptTokens(res.tokenUsage().inputTokenCount())
                        .totalTokens(res.tokenUsage().totalTokenCount())
                        .build())
                .choices(list)
                .build();
    }

    @Data
    @Builder
    static class Usage {
        private final Integer promptTokens;
        private final Integer completionTokens;
        private final Integer totalTokens;
    }

    @Data
    @Builder
    static class ChatCompletionChoice {
        private final Delta delta;
        private final String finishReason;
    }

    @Data
    @Builder
    static class Delta {
        private final String content;
    }
}
