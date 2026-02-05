package work.metanet.feng.admin.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import work.metanet.feng.common.core.util.ValidGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 项目表(Project)表实体类
 *
 * @author edison
 * @since 2023-10-01
 */

@Data
@Schema(description = "SysProject")
@EqualsAndHashCode(callSuper = true)
public class SysProject extends Model<SysProject> {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "项目ID")
    private Integer id;

    /**
     * 项目名称
     */
    @NotBlank(message = "项目名称不能为空", groups = {ValidGroup.Save.class, ValidGroup.Update.class})
    @Schema(description = "项目名称")
    private String projectName;

    /**
     * 项目编码
     */
    @NotBlank(message = "项目编码不能为空", groups = {ValidGroup.Save.class, ValidGroup.Update.class})
    @Schema(description = "项目编码")
    private String projectCode;

    /**
     * 项目描述
     */
    @Schema(description = "项目描述")
    private String projectDesc;

    /**
     * 所属机构编码
     */
    @NotBlank(message = "所属机构编码不能为空", groups = {ValidGroup.Save.class, ValidGroup.Update.class})
    @Schema(description = "所属机构编码")
    private String organCode;

    /**
     * 所属科室ID
     */
    @Schema(description = "所属科室ID")
    private Integer deptId;

    /**
     * 项目经理工号
     */
    @Schema(description = "项目经理工号")
    private String projectManagerStaffNo;

    /**
     * 项目经理姓名
     */
    @Schema(description = "项目经理姓名")
    private String projectManagerName;

    /**
     * 项目开始日期
     */
    @Schema(description = "项目开始日期")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date startDate;

    /**
     * 项目结束日期
     */
    @Schema(description = "项目结束日期")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date endDate;

    /**
     * 项目状态代码
     */
    @Schema(description = "项目状态代码")
    private String statusCode;

    /**
     * 项目状态名称
     */
    @Schema(description = "项目状态名称")
    private String statusName;

    /**
     * 项目预算
     */
    @Schema(description = "项目预算")
    private BigDecimal budget;

    /**
     * 实际成本
     */
    @Schema(description = "实际成本")
    private BigDecimal actualCost;

    /**
     * 项目进度（百分比）
     */
    @Schema(description = "项目进度（百分比）")
    private BigDecimal progress;

    /**
     * 项目优先级代码
     */
    @Schema(description = "项目优先级代码")
    private String priorityCode;

    /**
     * 项目优先级名称
     */
    @Schema(description = "项目优先级名称")
    private String priorityName;

    /**
     * 状态:0-启用 1-禁用
     */
    @Schema(description = "状态:0-启用 1-禁用")
    private String status;

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