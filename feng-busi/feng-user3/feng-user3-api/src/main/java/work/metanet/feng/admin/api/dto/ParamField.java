package work.metanet.feng.admin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Schema(description = "允许参与查询的字段")
public class ParamField {
    /**字段ID*/
    @Schema(description = "字段ID")
    private String fieldId;
    /**字段名*/
    @Schema(description = "字段名")
    private String fieldName;
    /**字段中文名*/
    @Schema(description = "字段中文名")
    private String fieldNameChinese;
    /**字段类型*/
    @Schema(description = "字段类型")
    private String fieldType;
}
