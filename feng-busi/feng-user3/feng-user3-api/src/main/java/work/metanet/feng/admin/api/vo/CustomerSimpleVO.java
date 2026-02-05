package work.metanet.feng.admin.api.vo;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "客户简略视图对象")
public class CustomerSimpleVO implements Serializable {

	private static final long serialVersionUID = 1L;

	@Schema(description = "客户ID")
    private Long id;
    
    @Schema(description = "客户名称")
    private String name;
    
    @Schema(description = "客户级别")
    private String levelCode;
}