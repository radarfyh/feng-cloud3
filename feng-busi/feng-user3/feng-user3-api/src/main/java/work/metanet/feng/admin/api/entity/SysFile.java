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
 * 文件管理表(SysFile)表实体类
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Data
@Schema(description = "SysFile")
@EqualsAndHashCode(callSuper = true)
public class SysFile extends Model<SysFile> {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "编号")
    private Long id;
    /**
     * 文件名
     */
    @Schema(description = "文件名")
    private String fileName;
    /**
     * 桶名
     */
    @Schema(description = "桶名")
    private String bucketName;

    @Schema(description = "")
    private String original;
    /**
     * 类型
     */
    @Schema(description = "类型")
    private String type;
    /**
     * 文件大小
     */
    @Schema(description = "文件大小")
    private Long fileSize;
    /**
     * 所属机构编码
     */
    @Schema(description = "所属机构编码")
    private String organCode;
    /**
     * 创建者
     */
    @Schema(description = "创建者")
    private String createBy;
    /**
     * 上传时间
     */
    @Schema(description = "上传时间")
    private LocalDateTime createTime;

    @Schema(description = "")
    private String updateBy;
    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "")
    @TableLogic
    private String delFlag;


}