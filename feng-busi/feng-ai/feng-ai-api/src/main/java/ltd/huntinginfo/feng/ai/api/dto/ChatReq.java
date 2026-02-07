package ltd.huntinginfo.feng.ai.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Data;
import lombok.experimental.Accessors;
import ltd.huntinginfo.feng.ai.api.entity.AigcPrompt;
import ltd.huntinginfo.feng.common.core.constant.enums.ProviderEnum;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

/**
 * 聊天请求DTO类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Data
@Accessors(chain = true)
@Schema(name = "聊天请求",description = "聊天请求DTO")
public class ChatReq {

	@Schema(description = "应用ID")
    private Integer appId;
	
	@Schema(description = "应用渠道ID")
    private Integer appApiId;
	
	@Schema(description = "模型ID")
    private Integer modelId;
	
	@Schema(description = "模型名称")
    private String modelName;
	
    @Schema(description = "模型供应商")
    private String modelProvider;
    
    @Schema(description = "消息内容", requiredMode = RequiredMode.REQUIRED)
    private String message;
    
    @Schema(description = "会话ID", requiredMode = RequiredMode.REQUIRED)
    private Integer conversationId;
    
    @Schema(description = "用户ID")
    private Integer userId;
    
    @Schema(description = "用户名称")
    private String username;
    
    @Schema(description = "子会话ID")
    private String chatId;
    
    @Schema(description = "提示语")
    private List<AigcPrompt> prompts;
    
    @Schema(description = "文档名")
    private String docsName;
    
    @Schema(description = "知识ID")
    private Integer knowledgeId;
    
    @Schema(description = "知识ID集合")
    private List<Integer> knowledgeIds = new ArrayList<>();
    
    @Schema(description = "文档ID")
    private Integer docsId;
    
    @Schema(description = "链接")
    private String url;
    
    @Schema(description = "角色", requiredMode = RequiredMode.REQUIRED)
    private String role;
    
    public ChatReq() {}
    
    public ChatReq(ChatReq data) {
    	BeanUtils.copyProperties(this, data);
    }
}
