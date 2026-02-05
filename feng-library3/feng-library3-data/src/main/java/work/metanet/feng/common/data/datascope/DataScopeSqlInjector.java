package work.metanet.feng.common.data.datascope;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

/**
 * 自定义 MyBatis-Plus 数据权限 SQL 注入器
 * <p>
 * 该类扩展了 MyBatis-Plus 的 DefaultSqlInjector，提供了自定义数据权限相关的方法注入。通过注入自定义的
 * 数据权限查询方法，增强了 MyBatis 的查询功能，支持基于数据权限的动态 SQL 生成。
 * </p>
 */
public class DataScopeSqlInjector extends DefaultSqlInjector {

    /**
     * 获取所有自定义的 SQL 方法列表，并添加数据权限相关的查询方法
     * <p>
     * 该方法会在原有的 SQL 注入方法基础上，添加自定义的基于数据权限的查询方法，覆盖默认的行为。
     * 包括：基于数据权限的查询列表、分页查询、以及计数查询。
     * </p>
     * 
     * @param mapperClass 当前 Mapper 类
     * @param tableInfo 当前表的信息
     * @return 包含自定义数据权限方法的 SQL 方法列表
     */
    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        // 获取父类的默认方法列表
        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
        
        // 确保 methodList 不为 null，避免空指针异常
        if (methodList == null) {
            methodList = new ArrayList<>();
        }

        // 添加自定义的数据权限相关方法
        addUniqueMethod(methodList, new SelectListByScope());
        addUniqueMethod(methodList, new SelectPageByScope());
        addUniqueMethod(methodList, new SelectCountByScope());

        return methodList;
    }

    /**
     * 向方法列表中添加方法，如果方法列表中不存在该方法，则添加
     * <p>
     * 该方法用于确保不会重复添加相同的 SQL 方法到 methodList 中
     * </p>
     * 
     * @param methodList 方法列表
     * @param method 要添加的方法
     */
    private void addUniqueMethod(List<AbstractMethod> methodList, AbstractMethod method) {
        // 检查方法列表中是否已经存在该方法
        Optional<AbstractMethod> existingMethod = methodList.stream()
                .filter(m -> m.getClass().equals(method.getClass()))
                .findFirst();
        
        // 如果方法列表中没有该方法，则添加
        if (!existingMethod.isPresent()) {
            methodList.add(method);
        }
    }
}

