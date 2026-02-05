package work.metanet.feng.ai.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 聊天应答DTO类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */
@Data
@Accessors(chain = true)
@Schema(name = "聊天应答",description = "聊天应答DTO")
public class ChatRes {

	@Schema(description = "是否已完成，默认为否")
    private boolean isDone = false;
	@Schema(description = "消息内容")
    private String message;
	@Schema(description = "已使用令牌数量")
    private Integer usedToken;
	@Schema(description = "时间")
    private long time;

    public ChatRes(String message) {
        this.message = message;
    }

    public ChatRes(Integer usedToken, long startTime) {
        this.isDone = true;
        this.usedToken = usedToken;
        this.time = System.currentTimeMillis() - startTime;
    }
}
