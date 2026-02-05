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
 * 路由配置表(SysRouteConf)表实体类
 *
 * @author edison
 * @since 2022-05-11 16:04:07
 */
@Data
@Schema(description = "SysRouteConf")
@EqualsAndHashCode(callSuper = true)
public class SysRouteConf extends Model<SysRouteConf> {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键")
    private Integer id;
    /**
     * 路由名称
     */
    @Schema(description = "路由名称")
    private String routeName;
    /**
     * 路由id
     */
    @Schema(description = "路由id")
    private String routeId;
    /**
     * 断言
     */
    @Schema(description = "断言")
    private Object predicates;
    /**
     * 过滤器，用于微服务接口限流过滤等作用
     */
    @Schema(description = "过滤器，用于微服务接口限流过滤等作用")
    private Object filters;
    /**
     * 路由url
     */
    @Schema(description = "路由url")
    private String uri;
    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sortOrder;
    /**
     * 元数据
     */
    @Schema(description = "元数据")
    private String metaData;
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
    /**
     * 逻辑删 0-正常 1-删除
     */
    @Schema(description = "逻辑删 0-正常 1-删除")
    @TableLogic
    private String delFlag;


}