package work.metanet.feng.common.core.constant;

/**
 * 分页相关的常量
 * <p>
 * 该接口定义了分页相关的常量，用于分页查询时传递当前页和每页大小等参数。
 * </p>
 */
public interface PaginationConstants {

    /**
     * 当前页，表示查询结果中的第几页
     */
    String CURRENT = "current";

    /**
     * 每页显示的记录数，表示每一页包含的条目数
     */
    String SIZE = "size";

    /**
     * 默认的当前页，通常是第一页
     */
    int DEFAULT_CURRENT_PAGE = 1;

    /**
     * 默认的每页大小，通常是10条记录
     */
    int DEFAULT_PAGE_SIZE = 10;
}
