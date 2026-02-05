package work.metanet.feng.admin.api.vo;

import work.metanet.feng.admin.api.entity.SysProject;

import java.math.BigDecimal;
import java.util.Date;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 项目表(SysProject)视图对象
 * <p>
 * 该类用于封装项目的视图层数据，继承自 SysProject 实体类，并添加了额外的字段，如项目的详细信息展示。
 * </p>
 * 
 * @author edison
 * @date 2023-10-01
 */
@Data
@Schema(description = "项目信息VO")
public class SysProjectVO extends SysProject {

    /**
     * 项目经理姓名
     * <p>
     * 该字段表示项目经理的姓名，用于项目展示。
     * </p>
     */
    @Schema(description = "项目经理姓名")
    private String projectManagerName;

    /**
     * 项目状态名称
     * <p>
     * 该字段表示项目的状态名称，用于在视图中展示项目的状态。
     * </p>
     */
    @Schema(description = "项目状态名称")
    private String statusName;

    /**
     * 项目优先级名称
     * <p>
     * 该字段表示项目的优先级名称，用于项目展示。
     * </p>
     */
    @Schema(description = "项目优先级名称")
    private String priorityName;

    /**
     * 项目预算
     * <p>
     * 该字段表示项目的预算，用于展示项目的财务信息。
     * </p>
     */
    @Schema(description = "项目预算")
    private BigDecimal budget;

    /**
     * 项目实际成本
     * <p>
     * 该字段表示项目的实际花费成本，用于展示项目的财务信息。
     * </p>
     */
    @Schema(description = "项目实际成本")
    private BigDecimal actualCost;

    /**
     * 项目进度（百分比）
     * <p>
     * 该字段表示项目当前的进度百分比，用于项目展示。
     * </p>
     */
    @Schema(description = "项目进度（百分比）")
    private BigDecimal progress;

    /**
     * 项目开始日期
     * <p>
     * 该字段表示项目的开始时间。
     * </p>
     */
    @Schema(description = "项目开始日期")
    private Date startDate;

    /**
     * 项目结束日期
     * <p>
     * 该字段表示项目的结束时间。
     * </p>
     */
    @Schema(description = "项目结束日期")
    private Date endDate;

    /**
     * 项目描述
     * <p>
     * 该字段用于详细描述项目的背景、目标、实施计划等信息。
     * </p>
     */
    @Schema(description = "项目描述")
    private String projectDesc;
}

