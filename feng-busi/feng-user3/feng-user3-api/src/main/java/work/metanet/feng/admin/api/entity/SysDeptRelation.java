package work.metanet.feng.admin.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 科室关系表(SysDeptRelation)表实体类
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Data
@Schema(description = "SysDeptRelation")
@EqualsAndHashCode(callSuper = true)
public class SysDeptRelation extends Model<SysDeptRelation> {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Integer id;

    /**祖先节点*/    
    @Schema(description = "祖先节点")
    private Integer ancestor;
    /**后代节点*/    
    @Schema(description = "后代节点")
    private Integer descendant;


    }