package work.metanet.feng.admin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "查询主题")
public class QueryThemeDetail {
    @Schema(description = "数据源ID")
    private String datasourceId;
    @Schema(description = "数据源名称")
    private String datasourceName;
    @Schema(description = "允许参与查询的表")
    private List<ParamTable> paramTableList;
    @Schema(description = "实际参与查询的表")
    private List<QueryTable> tableList;
    @Schema(description = "展示的字段")
    private List<QueryField> showFieldList;
    @Schema(description = "查询关联条件")
    private List<QueryAssociated> associatedList;
    @Schema(description = "查询条件")
    private List<QueryCondition> queryConditionVOList;
    @Schema(description = "查询排序")
    private List<QueryField> orderByList;

}
