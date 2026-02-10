package ltd.huntinginfo.feng.agent.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

//MSG-1011 请求DTO

@Data
@Schema(name = "消息查询请求DTO", description = "MSG-1011接口请求参数")
public class MsgQueryRequestDTO {

	@Schema(description = "查询类型：1-查询最近收到的消息 2-查询未读消息", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {
			"1", "2" })
	private String type;
	
	@Schema(description = "最大记录数")
	private Integer limit = 20;

	@Schema(description = "页码")
	private Integer pageNum = 1;

	@Schema(description = "每页数量")
	private Integer pageSize = 20;
}
