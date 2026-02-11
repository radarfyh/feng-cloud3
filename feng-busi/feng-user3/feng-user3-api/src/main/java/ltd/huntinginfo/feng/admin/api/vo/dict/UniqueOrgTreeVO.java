package ltd.huntinginfo.feng.admin.api.vo.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 统一机构树形 VO
 */
@Data
@Schema(description = "统一机构树节点")
public class UniqueOrgTreeVO {

    @Schema(description = "主键ID")
    private String id;

    @Schema(description = "机构编号")
    private String orgId;

    @Schema(description = "机构名称")
    private String orgName;

    @Schema(description = "机构代码")
    private String orgCode;

    @Schema(description = "上级机构ID")
    private String parentId;

    @Schema(description = "排序号")
    private String orderId;

    @Schema(description = "子机构列表")
    private List<UniqueOrgTreeVO> children;
}
