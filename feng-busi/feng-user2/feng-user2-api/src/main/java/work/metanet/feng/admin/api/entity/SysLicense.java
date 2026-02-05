package work.metanet.feng.admin.api.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Schema(description = "许可证")
@EqualsAndHashCode(callSuper = true)
public class SysLicense extends Model<SysLicense> {
	private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Integer id;
	
    private String licenseKey;  // 许可证密钥
    private int maxUsers;       // 许可证允许的最大用户数
    private int maxCpu;         // 许可证绑定的最大CPU数
    private Date validUntil;    // 许可证有效期
    private String status;      // 许可证状态（valid, expired, revoked）
}
