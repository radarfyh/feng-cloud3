package work.metanet.feng.admin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@Schema(description = "允许参与查询的表")
public class ParamTable{
        /**表ID*/
        @Schema(description = "表ID")
        private String tableId;
        /**表英文名*/
        @Schema(description = "表英文名")
        private String tableName;
        /**表中文名*/
        @Schema(description = "表中文名")
        private String tableNameChinese;

        @Schema(description = "该给下允许参与查询的字段")
        private List<ParamField> paramFieldList;
}
