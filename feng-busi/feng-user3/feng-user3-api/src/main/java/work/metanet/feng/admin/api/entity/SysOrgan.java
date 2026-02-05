package work.metanet.feng.admin.api.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import work.metanet.feng.common.core.util.ValidGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 机构表(SysOrgan)表实体类
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Data
@Schema(description = "SysOrgan")
@EqualsAndHashCode(callSuper = true)
public class SysOrgan extends Model<SysOrgan> {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "id")
    private Integer id;
    
    @Schema(description = "所属租户ID")
    private Integer tenantId;
    /**
     * 机构名称
     */
    @NotBlank(message = "机构名称不能为空", groups = {ValidGroup.Save.class, ValidGroup.Update.class})
    @Schema(description = "机构名称")
    private String organName;
    /**
     * 机构编码
     */
    @NotBlank(message = "机构编码不能为空", groups = {ValidGroup.Save.class, ValidGroup.Update.class})
    @Schema(description = "机构编码")
    private String organCode;
    /**
     * 机构类型
     */
    @NotBlank(message = "机构类型不能为空", groups = {ValidGroup.Save.class})
    @Schema(description = "机构类型")
    private String organType;
    /**
     * 默认密码
     */
    @NotBlank(message = "默认密码不能为空", groups = {ValidGroup.Save.class})
    @Schema(description = "默认密码")
    private String defaultPassword;
    /**
     * 第二名称（简称）
     */
    @Schema(description = "第二名称（简称）")
    private String organAliasName;
    /**
     * 上级机构
     */
    @Schema(description = "上级机构")
    private Integer parentId;
    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sort;
    /**
     * 机构类别编码
     */
    @Schema(description = "机构类别编码")
    private String organCategoryCode;
    /**
     * 机构类别名称
     */
    @Schema(description = "机构类别名称")
    private String organCategoryName;
    /**
     * 经济类型编码
     */
    
    @Schema(description = "经济类型编码")
    private String economicTypeCode;
    /**
     * 经济类型名称
     */
    
    @Schema(description = "经济类型名称")
    private String economicTypeName;
    /**
     * 分类管理代码
     */
    
    @Schema(description = "分类管理代码")
    private String manageClassCode;
    /**
     * 分类管理名称
     */
    
    @Schema(description = "分类管理名称")
    private String manageClassName;
    /**
     * 详细地址
     */
    @Schema(description = "详细地址")
    private String address;
    /**
     * 地址-省
     */
    @Schema(description = "地址-省")
    private String addrProvince;
    /**
     * 地址-市
     */
    @Schema(description = "地址-市")
    private String addrCity;
    /**
     * 地址-县
     */
    @Schema(description = "地址-县")
    private String addrCounty;
    /**
     * 地址-乡
     */
    @Schema(description = "地址-乡")
    private String addrTown;
    /**
     * 地址-村
     */
    @Schema(description = "地址-村")
    private String addrVillage;
    /**
     * 地址-门牌号
     */
    @Schema(description = "地址-门牌号")
    private String addrHouseNo;
    /**
     * 行政区划
     */
    @Schema(description = "行政区划")
    private String administrativeDivision;
    /**
     * 邮编
     */
    @Schema(description = "邮编")
    private String zipCode;
    /**
     * 电话
     */
    @Schema(description = "电话")
    private String telephone;
    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;
    /**
     * 网址
     */
    @Schema(description = "网址")
    private String website;
    /**
     * 成立日期
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Schema(description = "成立日期")
    private String establishDate;
    /**
     * 机构介绍
     */
    @Schema(description = "机构介绍")
    private String organIntroduction;
    /**
     * 交通路线
     */
    @Schema(description = "交通路线")
    private String trafficRoute;
    /**
     * 审批机关
     */
    @Schema(description = "审批机关")
    private String approvalAuthority;
    /**
     * 登记号
     */
    @Schema(description = "登记号")
    private String registerNo;
    /**
     * 法人
     */
    @Schema(description = "法人")
    private String legalPerson;
    /**
     * 主要负责人
     */
    @Schema(description = "主要负责人")
    private String principalName;
    /**
     * 负责人电话
     */
    @Schema(description = "负责人电话")
    private String principalTelecom;
    /**
     * 执业许可开始日期
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Schema(description = "执业许可开始日期")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    private Date licenseStartDate;
    /**
     * 执业许可结束日期
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Schema(description = "执业许可结束日期")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    private Date licenseEndDate;
    /**
     * 机构图片
     */
    @Schema(description = "机构图片地址")
    private String organPictures;
    /**
     * 资质图片
     */
    @Schema(description = "资质图片地址")
    private String licensePictures;
    
    /**
     * 派出（分支）机构数量
     */
    @Schema(description = "派出（分支）机构数量")
    private Integer branchingQuantity;

    /**
     * 员工数
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Schema(description = "员工数")
    private Integer staffQuantity;
    /**
     * 业务量
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Schema(description = "业务量")
    private Integer dailyVisits;
    /**
     * 产品数
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Schema(description = "产品数")
    private Integer productQuantity;
    /**
     * 状态
     */
    @Schema(description = "状态")
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
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    /**
     * 逻辑删 0-正常 1-删除
     */
    @Schema(description = "逻辑删 0-正常 1-删除")
    @TableLogic
    private String delFlag;


}