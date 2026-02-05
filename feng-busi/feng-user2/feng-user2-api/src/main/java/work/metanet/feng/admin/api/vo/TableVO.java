package work.metanet.feng.admin.api.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import work.metanet.feng.admin.api.entity.SysTable;

@Data
@Schema(description = "数据源VO")
public class TableVO extends SysTable {
	@Schema(description = "使用检测")
    //true 已使用，false 未使用
    private boolean enableCheck;
}
