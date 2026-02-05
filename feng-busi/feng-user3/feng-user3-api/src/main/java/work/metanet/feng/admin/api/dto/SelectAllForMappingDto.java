package work.metanet.feng.admin.api.dto;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "SelectAllNewDto")
public class SelectAllForMappingDto {

	@Schema(description = "值域代码标识符")
    @NotEmpty(message = "值域代码标识符不能为空")
    private String identifier;

	@Schema(description = "编码")
    private String code;

    @Schema(description = "值")
    private String value;

    @Schema(description = "排序字段")
    private String sortBy;


    @Schema(description = "对照类型(1:已对照; 0:未对照)")
    private String mappingType;
    @Schema(description = "关联方模型ID")
    private String mappingModelId;

    @Schema(description = "关联方模型ID",hidden = true)
    private String mappingId;
}
