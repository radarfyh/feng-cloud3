package work.metanet.feng.common.data.datascope;

import java.util.List;

/**
 * 数据权限判断处理器接口
 * <p>
 * 该接口用于处理数据权限的判断逻辑，通过传入部门 ID 列表来决定是否需要对数据进行过滤处理。
 * 实现该接口的类可以根据不同的权限模型，决定是否需要根据部门 ID 执行数据权限过滤。
 * </p>
 */
public interface DataScopeHandle {

    /**
     * 判断是否需要进行数据过滤处理
     * <p>
     * 根据用户的部门权限数据，判断是否需要对数据进行过滤。如果返回 true，则表示
     * 无需进行过滤；返回 false 则表示需要对数据进行过滤。
     * </p>
     *
     * @param deptList 用户的部门 ID 列表，如果为空表示没有任何数据权限。
     * @return 返回是否需要进行数据过滤。true 表示无需过滤，false 表示需要过滤。
     */
    boolean shouldFilterData(List<Integer> deptList);

}

