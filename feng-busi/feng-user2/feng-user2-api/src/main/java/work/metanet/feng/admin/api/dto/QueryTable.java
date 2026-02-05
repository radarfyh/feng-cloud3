package work.metanet.feng.admin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "参与查询的表")
public class QueryTable {
    /**表ID*/
    @Schema(description = "表ID")
    private String tableId;
    /**表英文名*/
    @Schema(description = "表英文名")
    private String tableName;
    /**表中文名*/
    @Schema(description = "表中文名")
    private String tableNameChinese;
    /**是否保留表* true 是,false 否*/
    @Builder.Default
    @Schema(description = "是否保留表* true 是,false 否 默认为是")
     private boolean reserve = true;
}
