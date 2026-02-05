package work.metanet.feng.admin.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 厂商表(SysManufacturer)表实体类
 *
 * @author edison
 * @since 2022-06-09 15:03:37
 */
@Data
@Schema(description = "SysManufacturer")
@EqualsAndHashCode(callSuper = true)
public class SysManufacturer extends Model<SysManufacturer> {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)   
    @Schema(description = "id")
    private Integer id;
    
    @Schema(description = "所属租户ID")
    private Integer tenantId;
    
    /**厂商名称*/    
    @Schema(description = "厂商名称")
    private String manufacturerName;
    /**厂商编码*/    
    @Schema(description = "厂商编码")
    private String manufacturerCode;
    /**技术人员姓名*/
    @Schema(description = "技术人员姓名")
    private String artisanName;
    /**技术人员手机号*/    
    @Schema(description = "技术人员手机号")
    private String artisanPhone;
    /**业务人员姓名*/    
    @Schema(description = "业务人员姓名")
    private String serviceName;
    /**业务人员手机号*/    
    @Schema(description = "业务人员手机号")
    private String servicePhone;
    /**厂商描述*/    
    @Schema(description = "厂商描述")
    private String manufacturerDesc;
    /**所属机构编码*/    
    @Schema(description = "所属机构编码")
    private String organCode;
    /**创建者*/    
    @Schema(description = "创建者")
    private String createBy;
    /**创建时间*/    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    /**更新者*/    
    @Schema(description = "更新者")
    private String updateBy;
    /**更新时间*/    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    /**逻辑删 0-正常 1-删除*/    
    @Schema(description = "逻辑删 0-正常 1-删除")
    @TableLogic
    private String delFlag;


    }