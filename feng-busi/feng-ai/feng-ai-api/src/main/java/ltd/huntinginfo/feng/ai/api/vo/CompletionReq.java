package ltd.huntinginfo.feng.ai.api.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 完成请求VO类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Data
@Builder
@Schema(name = "完成请求",description = "完成请求VO")
public class CompletionReq {
	@Schema(description = "模型ID，包括预置模型、用户自定义部署模型")
    private final String model;
	@Schema(description = "对话消息列表，每个消息均是消息对象，包含role和message两个部分")
    private final List<Message> messages;
	@Schema(description = "温度采样。一般取值范围 (0, 2)，控制生成的随机性，值比1大则会生成更加随机的文本；值比1小则生成的文本更加保守")
    private final Double temperature;
	@Schema(description = "top-p采样。一般取值范围 (0, 1]，控制模型生成过程中考虑的词汇范围，使用累计概率选择候选词，直到累计概率超过给定的阈值。取值越大，生成的随机性越高；取值越低，生成的确定性越高。")
    private final Double topP;
	@Schema(description = "1到n个候选者（choice）")
    private final Integer n;
	@Schema(description = "是否以流式接口的形式返回数据，默认为非流式")
    private final Boolean stream;
	@Schema(description = "停止列表，包含一到多个生成停止标识，当模型生成结果以stop中某个元素结尾时，停止文本生成")
    private final List<String> stop;
	@Schema(description = "最大令牌数目。一般取值范围 (0, 2048]。控制最大生成长度，超过该值则截断")
    private final Integer maxTokens;
	@Schema(description = "存在惩罚。一般取值范围[-2, 2]，影响模型如何根据到目前为止是否出现在文本中来惩罚新token。值大于0，将通过惩罚已经使用的词，增加模型谈论新主题的可能性")
    private final Double presencePenalty;
	@Schema(description = "频率惩罚。一般取值范围[-2, 2]，默认值为0.0，影响模型如何根据文本中词汇（token）的现有频率惩罚新词汇（token）。值大于0，会根据新标记在文本中的现有频率来惩罚新标记，从而降低模型逐字重复同一行的可能性")
    private final Double frequencyPenalty;
	@Schema(description = "用户ID，要求唯一")
    private final String user;
	@Schema(description = "随机种子。一般取值范围(0, 9223372036854775807]，用于指定推理过程的随机种子，相同的seed值可以确保推理结果的可重现性，不同的seed值会提升推理结果的随机性")
    private final Integer seed;

    @Data
    @Builder
    public static class Message {
        String role;
        String content;
    }
}
