package work.metanet.feng.admin.api.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 联盟机构关联表(SysAffiliationOrgan)实体类
 *
 * @author edison
 * @since 2023-08-02 09:53:01
 */
@Data
@Schema(description = "联盟机构关联表")
@EqualsAndHashCode(callSuper = true)
public class SysAffiliationOrgan extends Model<SysAffiliationOrgan> {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Integer id;

    /**
     * 联盟id
     */
    @Schema(description = "联盟id")
    private Integer affiliationId;
    /**
     * 机构id
     */
    @Schema(description = "机构id")
    private Integer organId;
    /**
     * 是否为主联盟机构
     */
    @Schema(description = "是否为主联盟机构")
    private String isLeader;
}
