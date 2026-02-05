package work.metanet.feng.admin.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * 用户会话表实体类
 * @author EdisonFeng
 * @since 2024-09-17
 */
@Data
@Schema(description = "UserSession")
@EqualsAndHashCode(callSuper = true)
public class SysUserSession extends Model<SysUserSession> {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Integer id;
    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;
    
    /**
     * 会话session id
     */
    @Schema(description = "会话id")
    private String sessionId;
    
    /**
     * 最后活跃时间
     */
    @Schema(description = "最后活跃时间")
    private Timestamp lastActive;
    /**
     * 状态
     */
    @Schema(description = "状态")
    private String status;

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

}