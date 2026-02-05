package work.metanet.feng.admin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 动态SQL执行类
 * @author EdisonFeng
 **/
@Data
public class DatasourceSqlDTO {

    /**
     * 数据源类型
     */
    @Schema(description = "数据源类型")
    private String dbType;

    /**
     * 数据源地址
     */
    @Schema(description = "数据源地址")
    private String jdbcUrl;

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

    @Schema(description = "数据库名")
    private String dbName;

    @Schema(description = "SQL语句")
    private String sql;
}
