package work.metanet.feng.admin.api.dto;

import lombok.Data;
import work.metanet.feng.admin.api.vo.TableFieldVO;
import work.metanet.feng.admin.api.vo.TableVO;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "表文件DTO")
public class TableFileDTO {
	@Schema(description = "表信息")
    private TableVO table;
	@Schema(description = "字段信息")
    private List<TableFieldVO> tableField;
}
