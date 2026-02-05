package work.metanet.feng.admin.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 终端信息表(SysOauthClientDetails)表实体类
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Data
@Schema(description = "SysOauthClientDetails")
@EqualsAndHashCode(callSuper = true)
public class SysOauthClientDetails extends Model<SysOauthClientDetails> {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "ID")
    private Integer id;
    
    @Schema(description = "所属租户ID")
    private Integer tenantId;
    
    /**
     * Oauth2对应的clientId
     */
    @Schema(description = "Oauth2对应的clientId")
    private String clientId;
    /**
     * 微服务id
     */
    @Schema(description = "微服务id")
    private String resourceIds;
    /**
     * 指定客户端(client)的访问密匙
     */
    @Schema(description = "指定客户端(client)的访问密匙")
    private String clientSecret;
    /**
     * 指定客户端申请的权限范围
     */
    @Schema(description = "指定客户端申请的权限范围")
    private String scope;
    /**
     * 授权方式[A,B,C]
     */
    @Schema(description = "授权方式[A,B,C]")
    private String[] authorizedGrantTypes;
    /**
     * 客户端的重定向URI,可为空
     */
    @Schema(description = "客户端的重定向URI,可为空")
    private String webServerRedirectUri;
    /**
     * 指定客户端所拥有的Spring Security的权限值
     */
    @Schema(description = "指定客户端所拥有的Spring Security的权限值")
    private String authorities;
    /**
     * 设定客户端的access_token的有效时间值
     */
    @Schema(description = "设定客户端的access_token的有效时间值")
    private Integer accessTokenValidity;
    /**
     * 设定客户端的refresh_token的有效时间值
     */
    @Schema(description = "设定客户端的refresh_token的有效时间值")
    private Integer refreshTokenValidity;
    /**
     * 扩展字段，本框架用于是否开启验证码模式
     */
    @Schema(description = "扩展字段，本框架用于是否开启验证码模式")
    private String additionalInformation;
    /**
     * 扩展字段，本框架用于是否开启验证码模式
     */
    @Schema(description = "扩展字段，本框架用于是否开启验证码模式")
    private String autoapprove;
    /**
     * 所属机构编码
     */
    @Schema(description = "所属机构编码")
    private String organCode;
    /**
     * 逻辑删 0-正常 1-删除
     */
    @Schema(description = "逻辑删 0-正常 1-删除")
    @TableLogic
    private String delFlag;


}