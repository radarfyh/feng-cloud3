package work.metanet.feng.common.data.datascope;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * FengBaseMapper 扩展了 MyBatis-Plus 的 BaseMapper 接口，提供了数据权限控制与批量插入的功能。
 * <p>
 * 该接口支持根据数据权限范围（DataScope）进行查询操作，例如：查询所有数据、分页查询数据、查询数据总数等。
 * </p>
 */
public interface FengBaseMapper<T> extends BaseMapper<T> {

    /**
     * 根据实体条件查询所有记录，支持数据权限控制。
     * <p>
     * 根据传入的 `queryWrapper` 条件和 `scope` 数据权限范围，查询符合条件的所有记录。
     * </p>
     * 
     * @param queryWrapper 实体对象封装操作类（可以为 null），用于封装查询条件
     * @param scope 数据权限范围，用于限定用户的可查询数据范围
     * @return 符合条件的记录列表
     */
    List<T> selectListByScope(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper, DataScope scope);

    /**
     * 根据实体条件分页查询记录，支持数据权限控制。
     * <p>
     * 根据传入的 `queryWrapper` 条件和 `scope` 数据权限范围，分页查询符合条件的记录。
     * </p>
     * 
     * @param page 分页查询条件（可以为 RowBounds.DEFAULT），用于指定查询的分页信息
     * @param queryWrapper 实体对象封装操作类（可以为 null），用于封装查询条件
     * @param scope 数据权限范围，用于限定用户的可查询数据范围
     * @param <E> 分页查询的返回类型，通常为 IPage<T>
     * @return 分页查询结果
     */
    <E extends IPage<T>> E selectPageByScope(E page, @Param(Constants.WRAPPER) Wrapper<T> queryWrapper, DataScope scope);

    /**
     * 根据条件查询记录总数，支持数据权限控制。
     * <p>
     * 根据传入的 `queryWrapper` 条件和 `scope` 数据权限范围，查询符合条件的记录总数。
     * </p>
     * 
     * @param queryWrapper 实体对象封装操作类（可以为 null），用于封装查询条件
     * @param scope 数据权限范围，用于限定用户的可查询数据范围
     * @return 符合条件的记录总数
     */
    Long selectCountByScope(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper, DataScope scope);

}
