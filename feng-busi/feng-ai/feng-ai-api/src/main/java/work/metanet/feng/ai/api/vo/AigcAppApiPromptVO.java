package work.metanet.feng.ai.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import work.metanet.feng.ai.api.entity.AigcPrompt;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * AigcAppApi 的提示语返回视图对象
 *
 * @author edison
 * @date 2025-04-25
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Data
@Accessors(chain = true)
@Schema(name = "生成式应用渠道提示语返回对象")
public class AigcAppApiPromptVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "渠道ID")
    private Integer id;

    @Schema(description = "应用ID")
    private Integer appId;

    @Schema(description = "API KEY")
    private String apiKey;

    @Schema(description = "应用渠道")
    private String channel;

    @Schema(description = "状态：0-正常，1-异常")
    private String status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

    @Schema(description = "提示语")
    private List<AigcPrompt> prompts;

}
