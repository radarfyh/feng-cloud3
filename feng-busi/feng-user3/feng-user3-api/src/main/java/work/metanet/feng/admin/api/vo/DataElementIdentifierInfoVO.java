package work.metanet.feng.admin.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "数据元标识符与数据模型关系")
public class DataElementIdentifierInfoVO {
    @Schema(description = "数据源ID")
    private String datasourceId;
/*    @Schema(description = "数据源名称")
    private String datasourceName;
    @Schema(description = "数据源中文名称")
    private String datasourceNameChinese;*/
    @Schema(description = "表ID")
    private String tableId;
    @Schema(description = "表名称")
    private String tableName;
    @Schema(description = "表中文名称")
    private String tableNameChinese;
    @Schema(description = "字段ID")
    private String fieldId;
    @Schema(description = "字段名称")
    private String fieldName;
    @Schema(description = "字段中文名称")
    private String fieldNameChinese;
    @Schema(description = "字段类型")
    private String fieldType;
    @Schema(description = "数据元标识符")
    private String  identifier;
}
