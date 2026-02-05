package work.metanet.feng.admin.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import work.metanet.feng.admin.api.entity.SysTableField;

@Data
@Schema(description = "表字段VO")
public class TableFieldVO extends SysTableField {

	@Schema(description = "表中文名")
    private String tableNameChinese;
}
