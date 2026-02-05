

package work.metanet.feng.admin.api.dto;

import com.baomidou.mybatisplus.annotation.TableLogic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author CC
 * @date:2021/1/8
 */
@Data
@Schema(description = "科室树")
@EqualsAndHashCode(callSuper = true)
public class SysDepartmentTree extends TreeNode implements Serializable {

    /**
     * 科室名称
     */
    @Schema(description = "科室名称")
    private String deptName;
    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sort;
    /**
     * 所属机构编码
     */
    @Schema(description = "所属机构编码")
    private String organCode;
    /**
     * 科目编码
     */
    @Schema(description = "科目编码")
    private String subjectCode;
    /**
     * 科目名称
     */
    @Schema(description = "科目名称")
    private String subjectName;
    /**
     * 科室类别编码
     */
    @Schema(description = "科室类别编码")
    private String deptCategoryCode;
    /**
     * 科室类别名称
     */
    @Schema(description = "科室类别名称")
    private String deptCategoryName;
    /**
     * 业务隶属
     */
    @Schema(description = "业务隶属 0：不区分 1:业务1 2：业务2  9.其他")
    private String businessSubjection;
    /**
     * 科室位置
     */
    @Schema(description = "科室位置")
    private String deptLocation;
    /**
     * 科室简介
     */
    @Schema(description = "科室简介")
    private String deptIntroduction;
    /**
     * 分支编码
     */
    @Schema(description = "分支编码")
    private String branchCode;
    /**
     * 分支名称
     */
    @Schema(description = "分支名称")
    private String branchName;
    /**
     * 创建者
     */
    @Schema(description = "创建者")
    private String createBy;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    /**
     * 更新者
     */
    @Schema(description = "更新者")
    private String updateBy;
    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
    /**
     * 逻辑删 0-正常 1-删除
     */
    @Schema(description = "逻辑删 0-正常 1-删除")
    @TableLogic
    private String delFlag;
}
