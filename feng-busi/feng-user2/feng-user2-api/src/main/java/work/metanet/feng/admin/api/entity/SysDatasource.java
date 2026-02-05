package work.metanet.feng.admin.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
//import work.metanet.feng.admin.api.handler.PgJsonTypeHandler;
import lombok.experimental.Accessors;

import java.util.Date;

import org.springframework.boot.jackson.JsonObjectDeserializer;

/**
 * 数据源表(SysDatasource)表实体类
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Data
@Schema(description = "SysDatasource")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "sys_datasource", autoResultMap = true)
public class SysDatasource extends Model<SysDatasource> {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "id")
    private Integer id;
    
    /**
     * 数据源连接名
     */
    @Schema(description = "数据源连接名")
    private String name;
    
    /**
     * 数据源类型
     */
    @Schema(description = "数据库类型")
    private String dbType;
    
    /**
     * 配置类型 （0 主机形式 | 1 url形式）
     */
    @Schema(description = "配置类型 （0 主机形式 | 1 url形式）")
    private String confType;
    
    /**
     * 主机host地址
     */
    @Schema(description = "主机host地址")
    private String host;
    
    /**
     * 主机端口号
     */
    @Schema(description = "主机端口号")
    private Integer port;
    
    /**
     * 拼接后的数据源地址
     */
    @Schema(description = "拼接后的数据源地址")
    private String url;
    
    /**
     * 数据库名称
     */
    @Schema(description = "数据库名称")
    private String dbName;
    
    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;
    
    /**
     * 密码
     */
    @Schema(description = "密码")
    private String password;
    
    /**
     * 应用编码
     */
    @Schema(description = "应用编码")
    private String applicationCode;
    
    /**
     * 变量入参
     */
    @Schema(description = "变量入参")
    @TableField(value = "var_parameter", typeHandler = JacksonTypeHandler.class)
    private Object varParameter;
    
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
    private Date createTime;
    
    /**
     * 更新者
     */
    @Schema(description = "更新者")
    private String updateBy;
    
    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private Date updateTime;
    
    /**
     * 逻辑删 0-正常 1-删除
     */
    @TableLogic
    @Schema(description = "逻辑删 0-正常 1-删除")
    private String delFlag;
}