

package work.metanet.feng.admin.api.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author CC
 * @date:2021/1/8
 */
@Data
@Schema(description = "科室树形节点")
public class TreeNode {
    @Schema(description = "当前科室节点ID")
    protected Integer id;

    @Schema(description = "父节点科室ID")
    protected Integer parentId;
    
    @Schema(description = "当前科室编码节点")
    protected String deptCode;

    @Schema(description = "父节点科室编码")
    protected String parentCode;

    /**
     * 是否包含子节点
     */
    @Schema(description = "是否包含子节点")
    private boolean hasChildren = false;

    @Schema(description = "子节点列表")
    protected List<TreeNode> children = null;

    public void add(TreeNode node) {
        children.add(node);
    }

}
