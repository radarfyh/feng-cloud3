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
 * 用户扩展属性配置(SysTableExtAttr)表实体类
 *
 * @author edison
 * @since 2022-05-11 16:04:07
 */
@Data
@Schema(description = "SysTableExtAttr")
@EqualsAndHashCode(callSuper = true)
public class SysTableExtAttr extends Model<SysTableExtAttr> {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "id")
    private Integer id;
    
    /**
     * 所依附表名
     */
    @Schema(description = "所依附表名")
    private String tableName;
    
    /**
     * 所依附记录ID
     */
    @Schema(description = "所依附记录ID")
    private String recordId;
    
    /**
     * 属性标识
     */
    @Schema(description = "属性标识")
    private String extKey;
    
    /**
     * 属性名称
     */
    @Schema(description = "属性名称")
    private String extName;
    
    /**
     * 属性取值类型
     */
    @Schema(description = "属性取值类型")
    private String extType;
    /**
     * 字典取值URL
     */
    @Schema(description = "字典取值URL")
    private String dictUrl;
    
    /**
     * 本地字典 json数组
     */
    @Schema(description = "本地字典 json数组")
    private String dictData;
    /**
     * 字典value属性
     */
    @Schema(description = "字典value属性")
    private String dictValue;
    
    /**
     * 字典label属性
     */
    @Schema(description = "字典label属性")
    private String dictLabel;
    
    /**
     * 界面填写样式
     */
    @Schema(description = "界面填写样式")
    private String fillInType;
    
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