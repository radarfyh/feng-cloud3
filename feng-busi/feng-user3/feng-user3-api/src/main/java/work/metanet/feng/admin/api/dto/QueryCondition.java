package work.metanet.feng.admin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "查询条件")
public class QueryCondition {
    @Schema(description = "表名")
    private String tableName;
    @Schema(description = "字段Id", requiredMode = RequiredMode.REQUIRED)
    private String fieldId;
    @Schema(description = "字段名称")
    private String fieldName;
    //@Schema(description = "条件 eq:等于,not_eq:不等于,lt:小于,le:小于等于,gt:大于,ge:大于等于,in:包含,not in:不包含,like:匹配,not like:不匹配,null:为空,not_null:不为空,between:区间 参数1到参数2之间",required = true)
    @Schema(description = "条件 eq:等于,not_eq:不等于,lt:小于,le:小于等于,gt:大于,ge:大于等于,between:区间",required = true)
    private String conditinion;
    @Schema(description = "条件值1", requiredMode = RequiredMode.REQUIRED)
    private String value1;
    @Schema(description = "条件值2")
    private String value2;
    @Builder.Default
    @Schema(description = " 且:and ,或:or 默认为且", requiredMode = RequiredMode.REQUIRED)
    private String logic = "and";
    @Schema(description = "数据类型")
    private String fieldType;
    @Schema(description = "前括号(")
    private String prefixBracket;
    @Schema(description = "后括号)")
    private String suffixBracket;

}
