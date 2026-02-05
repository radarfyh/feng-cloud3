package work.metanet.feng.admin.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 联盟信息表(SysAffiliation)实体类
 *
 * @author edison
 * @since 2023-08-02 09:50:08
 */
@Data
@Schema(description = "联盟信息表")
@EqualsAndHashCode(callSuper = true)
public class SysAffiliation extends Model<SysAffiliation> {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "id")
    private Integer id;
    /**
     * 联盟名称
     */
    @Schema(description = "联盟名称")
    private String affiliationName;
    /**
     * 联盟编码
     */
    @Schema(description = "联盟编码")
    private String affiliationCode;
    /**
     * 联盟简介
     */
    @Schema(description = "联盟简介")
    private String affiliationIntroduction;
    /**
     * 主要负责人
     */
    @Schema(description = "主要负责人")
    private String principalName;
    /**
     * 负责人电话
     */
    @Schema(description = "负责人电话")
    private String principalTelephone;
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
    private String delFlag;


}
