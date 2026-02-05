package work.metanet.feng.common.data.datascope;

import lombok.Data;
import lombok.EqualsAndHashCode;
import work.metanet.feng.common.core.constant.enums.DataScopeFuncEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 数据权限查询参数
 * <p>
 * 该类用于封装数据权限查询时所需的参数，包含了限制范围字段、用户数据权限范围、以及查询范围等信息。
 * </p>
 */
@Data
@EqualsAndHashCode
public class DataScope {

    /**
     * 限制范围的字段名称，默认是 "organCode"。
     * <p>
     * 用于指定数据表中与部门相关的字段名。
     * </p>
     */
    private String scopeDeptName = "organCode";

    /**
     * 本人权限范围字段，默认是 "create_by"。
     * <p>
     * 用于指定查询数据时与用户相关的字段名。
     * </p>
     */
    private String scopeUserName = "create_by";

    /**
     * 具体的数据范围，存储部门的 ID 列表。
     * <p>
     * 用于指定数据查询的具体部门范围。
     * </p>
     */
    private List<Integer> deptList = new ArrayList<>();

    /**
     * 具体查询的用户数据权限范围。
     * <p>
     * 用于指定需要查询的用户名。
     * </p>
     */
    private String username;

    /**
     * 是否只查询本部门的数据。
     * <p>
     * 如果为 true，查询只限制在当前部门的数据。
     * </p>
     */
    private Boolean isOnly = false;

    /**
     * 函数名称，默认使用 SELECT * 。
     * <p>
     * 允许设置查询时使用的函数，例如 COUNT(1)，用于指定查询函数。
     * </p>
     */
    private DataScopeFuncEnum func = DataScopeFuncEnum.ALL;

    /**
     * 获取 DataScope 实例的静态方法。
     * 
     * @return 返回一个新的 DataScope 实例
     */
    public static DataScope of() {
        return new DataScope();
    }

    /**
     * 设置部门 ID 列表。
     * 
     * @param deptIds 部门 ID 列表
     * @return 当前 DataScope 实例
     */
    public DataScope deptIds(List<Integer> deptIds) {
        this.deptList = deptIds;
        return this;
    }

    /**
     * 设置是否只查询本部门的数据。
     * 
     * @param isOnly 是否只查询本部门
     * @return 当前 DataScope 实例
     */
    public DataScope only(boolean isOnly) {
        this.isOnly = isOnly;
        return this;
    }

    /**
     * 设置查询时使用的函数。
     * 
     * @param func 查询函数枚举
     * @return 当前 DataScope 实例
     */
    public DataScope func(DataScopeFuncEnum func) {
        this.func = func;
        return this;
    }

}
