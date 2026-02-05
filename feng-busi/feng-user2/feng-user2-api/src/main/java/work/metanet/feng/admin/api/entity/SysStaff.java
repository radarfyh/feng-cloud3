package work.metanet.feng.admin.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import work.metanet.feng.common.core.constant.enums.Gender;
import work.metanet.feng.common.core.constant.enums.JobCategory;
import work.metanet.feng.common.core.constant.enums.Position;
import work.metanet.feng.common.core.util.ValidGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 人员信息表(SysStaff)表实体类
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Data
@Schema(description = "SysStaff")
@EqualsAndHashCode(callSuper = true)
public class SysStaff extends Model<SysStaff> {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "id")
    private Integer id;
    /**
     * 所属机构编码
     */
    @NotBlank(message = "所属机构编码不能为空", groups = {ValidGroup.Save.class, ValidGroup.Update.class})
    @Schema(description = "所属机构编码")
    private String organCode;
    /**
     * 所属科室id
     */
    @Schema(description = "所属科室id")
    private Integer deptId;
    /**
     * 工号
     */
    @NotBlank(message = "工号不能为空", groups = {ValidGroup.Save.class, ValidGroup.Update.class})
    @Schema(description = "工号")
    private String staffNo;
    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空", groups = {ValidGroup.Save.class, ValidGroup.Update.class})
    @Schema(description = "姓名")
    private String staffName;
    /**
     * 国籍编码 (GB/T 2659)
     */
    @Schema(description = "国籍编码 (GB/T 2659)")
    private String nationalityCode;
    /**
     * 国籍名称 (GB/T 2659)
     */
    @Schema(description = "国籍名称 (GB/T 2659)")
    private String nationalityName;
    /**
     * 民族编码 (GB/T 3304)
     */
    @Schema(description = "民族编码 (GB/T 3304)")
    private String nationCode;
    /**
     * 民族名称 (GB/T 3304)
     */
    @Schema(description = "民族名称 (GB/T 3304)")
    private String nationName;
    /**
     * 身份证号
     */
    @NotBlank(message = "身份证号不能为空", groups = {ValidGroup.Save.class, ValidGroup.Update.class})
    @Schema(description = "身份证号")
    private String identificationNo;
    /**
     * 性别代码 (GB/T 2261.1)
     */
    @Schema(description = "性别代码 (GB/T 2261.1)")
    @TableField("gender_code")
    private Gender genderCode;
    
    /**
     * 性别名称 (GB/T 2261.1)
     */
    @Schema(description = "性别名称")
    @TableField(exist = false)
    private String genderName;
    
    /**
     * 出生日期
     */
    @Schema(description = "出生日期")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    private Date birthdate;
    /**
     * 电话
     */
    @Schema(description = "电话")
    private String telephone;
    /**
     * 婚姻状况代码
     */
    @Schema(description = "婚姻状况代码")
    private String maritalStatusCode;
    /**
     * 婚姻状况名称
     */
    @Schema(description = "婚姻状况名称")
    private String maritalStatusName;
    /**
     * 籍贯
     */
    @Schema(description = "籍贯")
    private String nativePlace;
    /**
     * 政治面貌代码
     */
    @Schema(description = "政治面貌代码")
    private String politicsStatusCode;
    /**
     * 政治面貌名称
     */
    @Schema(description = "政治面貌名称")
    private String politicsStatusName;
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
    private Integer addrHouseNo;
    /**
     * 详细地址
     */
    @Schema(description = "详细地址")
    private String address;
    /**
     * 邮编
     */
    @Schema(description = "邮编")
    private String zipCode;
    /**
     * 学历代码 (GB/T 4658)
     */
    @Schema(description = "学历代码 (GB/T 4658)")
    private String educationLevelCode;
    /**
     * 学历名称 (GB/T 4658)
     */
    @Schema(description = "学历名称 (GB/T 4658)")
    private String educationLevelName;
    /**
     * 学位代码 (GB/T 6864)
     */
    @Schema(description = "学位代码 (GB/T 6864)")
    private String degreeCode;
    /**
     * 学位名称 (GB/T 6864)
     */
    @Schema(description = "学位名称 (GB/T 6864)")
    private String degreeName;
    /**
     * 专业代码 (GB/T 16835)
     */
    @Schema(description = "专业代码 (GB/T 16835)")
    private String subjectCode;
    /**
     * 专业名称 (GB/T 16835)
     */
    @Schema(description = "专业名称 (GB/T 16835)")
    private String subjectName;
    /**
     * 毕业院校
     */
    @Schema(description = "毕业院校")
    private String graduateSchoolName;
    /**
     * 参加工作日期
     */
    @Schema(description = "参加工作日期")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    private Date workBeginDate;
    /**
     * 岗位类别
     */
    @Schema(description = "岗位类别")
    private JobCategory jobCategory;
    /**
     * 专业技术职务代码 (GB/T 8561)
     */
    @Schema(description = "专业技术职务代码 (GB/T 8561)")
    private String technicalQualificationsCode;
    /**
     * 专业技术职务名称
     */
    @Schema(description = "专业技术职务名称")
    private String technicalQualificationsName;

    /**
     * 行政/业务管理职务代码 (GB/T 12403)
     */
    @Schema(description = "行政/业务管理职务代码 (GB/T 12403)")
    private String managementPositionCode;
    /**
     * 行政/业务管理职务名称
     */
    @Schema(description = "行政/业务管理职务名称")
    private String managementPositionName;

    /**
     * 职称代码
     */
    @Schema(description = "职称代码")
    private String titleCode;
    /**
     * 职称名称
     */
    @Schema(description = "职称名称")
    private String titleName;

    /**
     * 是否是编制人员
     */
    @Schema(description = "是否是编制人员")
    private String isOrganizational;
    /**
     * 职务，关联数据字典position
     */
    @Schema(description = "职务，关联数据字典position")
    private Position position;

    /**
     * 在岗状态代码
     */
    @Schema(description = "在岗状态代码")
    private String activeStatusCode;
    /**
     * 在岗状态名称
     */
    @Schema(description = "在岗状态名称")
    private String activeStatusName;
    /**
     * 资格证书编号
     */
    @Schema(description = "资格证书编号")
    private String qualificationCertificateNo;
    /**
     * 执业证书编号
     */
    @Schema(description = "执业证书编号")
    private String practisingCertificateNo;
    /**
     * 擅长领域
     */
    @Schema(description = "擅长领域")
    private String expertiseField;
    /**
     * 详细介绍
     */
    @Schema(description = "详细介绍")
    private String detailedIntroduction;
    /**
     * 一般人员
     */
    @Schema(description = "普通员工")
    private String isGeneralStaff;

    /**
     * 照片地址
     */
    @Schema(description = "照片地址")
    private String photograph;
    /**
     * 电子签名
     */
    @Schema(description = "电子签名")
    private String electronicSignature;
    /**
     * 资格证书
     */
    @Schema(description = "资格证书图片地址")
    private String qualificationCertificatePictures;
    /**
     * 执业证书
     */
    @Schema(description = "执业证书图片地址")
    private String practisingCertificatePictures;

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