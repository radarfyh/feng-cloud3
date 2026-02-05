//package work.metanet.feng.common.data.tenant;
//
//import com.baomidou.mybatisplus.annotation.TableName;
//import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
//
//import work.metanet.feng.admin.api.entity.SysRole;
//
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Field;
//
//import org.springframework.beans.factory.annotation.Autowired;
//
//public class DynamicTenantSqlParser extends TenantSqlParser { // TenantSqlParser需要Mybatis plus 3.1
//	@Autowired
//	FengTenantHandler tenantHandler;
//
//    @Override
//    public String process(String sql) {
//        String tenantIdColumn = getTenantIdColumnFromTable(SysRole.class);  // 假设你是根据 SysRole 类来获取表名
//        String tenantIdValue = tenantHandler.getTenantId().toString();
//
//        if (tenantIdValue != null && !tenantIdValue.isEmpty()) {
//            sql = sql + " AND " + tenantIdColumn + " = '" + tenantIdValue + "'";
//        }
//
//        return sql;
//    }
//
//    /**
//     * 使用反射获取实体类的 @TableName 注解中的表名
//     * @param entityClass 实体类
//     * @return 表名
//     */
//    private String getTenantIdColumnFromTable(Class<?> entityClass) {
//        // 通过反射获取类的 @TableName 注解
//        TableName tableNameAnnotation = entityClass.getAnnotation(TableName.class);
//
//        // 如果没有找到 @TableName 注解，抛出异常
//        if (tableNameAnnotation == null) {
//            throw new IllegalArgumentException("Entity class " + entityClass.getName() + " does not have a @TableName annotation.");
//        }
//
//        // 获取表名
//        String tableName = tableNameAnnotation.value();
//
//        // 根据表名拼接租户字段
//        return tableName + "." + tenantHandler.getTenantId().toString();  
//    }
//}
//
