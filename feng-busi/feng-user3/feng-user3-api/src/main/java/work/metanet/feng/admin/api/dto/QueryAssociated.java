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
@Schema(description = "关联查询")
public class QueryAssociated {
    @Schema(description = "前条件字段")
    private QueryField prefix;
    @Schema(description = "后条件字段")
    private QueryField suffix;
    @Builder.Default
    @Schema(description = " 且:and ,或:or 默认为且")
    private String logic = "and";

}
