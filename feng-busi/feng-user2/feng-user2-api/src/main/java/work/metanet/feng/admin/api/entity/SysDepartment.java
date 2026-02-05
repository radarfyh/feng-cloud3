package work.metanet.feng.admin.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import work.metanet.feng.common.core.util.ValidGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * 科室表(SysDepartment)表实体类
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Data
@Schema(description = "SysDepartment")
@EqualsAndHashCode(callSuper = true)
public class SysDepartment extends Model<SysDepartment> {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "id")
    private Integer id;
    /**
     * 科室编码
     */
    @NotBlank(message = "科室编码不能为空", groups = {ValidGroup.Save.class, ValidGroup.Update.class})
    @Schema(description = "科室编码")
    private String deptCode;
    /**
     * 科室名称
     */
    @NotBlank(message = "科室名称不能为空", groups = {ValidGroup.Save.class, ValidGroup.Update.class})
    @Schema(description = "科室名称")
    private String deptName;
    
    /**
     * 上级科室ID
     * 报错：jakarta.validation.UnexpectedTypeException: HV000030: No validator could be found for constraint 'jakarta.validation.constraints.NotBlank' validating type 'java.lang.Integer'
     * 解决方案：Integer不能使用@NotBlank，更换为@NotNull
     */
    //@NotBlank(message = "上级科室ID不能为空", groups = {ValidGroup.Update.class})
    @NotNull
    @Min(value = 0, message = "上级科室ID必须大于等于0")
    @Schema(description = "上级科室ID")
    private Integer parentId;
    
    /**
     * 上级科室编码
     */
    @NotBlank(message = "上级科室编码不能为空", groups = {ValidGroup.Update.class})
    @Schema(description = "上级科室编码")
    private String parentCode;
    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sort;
    /**
     * 所属机构编码
     */
    @NotBlank(message = "所属机构编码不能为空", groups = {ValidGroup.Save.class, ValidGroup.Update.class})
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