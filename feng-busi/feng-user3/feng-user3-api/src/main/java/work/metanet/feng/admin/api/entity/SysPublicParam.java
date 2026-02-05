package work.metanet.feng.admin.api.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 公共参数配置表(SysPublicParam)表实体类
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Data
@Schema(description = "SysPublicParam")
@EqualsAndHashCode(callSuper = true)
public class SysPublicParam extends Model<SysPublicParam> {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "编号")
    private Long id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "键")
    private String key;

    @Schema(description = "值")
    private String value;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "校验码")
    private String validateCode;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

    @Schema(description = "类型")
    private String type;

    @Schema(description = "是否系统内置，0-否，1-是")
    private String system;
    
    /**
     * 所属机构编码
     */
    @Schema(description = "所属机构编码")
    private String organCode;

    @Schema(description = "逻辑删 0-正常 1-删除")
    @TableLogic
    private String delFlag;
}