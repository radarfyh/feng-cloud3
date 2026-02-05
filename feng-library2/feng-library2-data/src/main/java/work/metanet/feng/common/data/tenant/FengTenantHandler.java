package work.metanet.feng.common.data.tenant;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.StringValue;
import work.metanet.feng.common.core.config.TenantOfHeader;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 租户维护处理器
 * <p>
 * 该类用于处理多租户环境下，针对不同租户的 SQL 查询条件拼接、租户ID的获取以及租户字段的处理。
 * </p>
 * <p>
 * 实现了 TenantLineHandler 接口，用于在 MyBatis 中自动处理租户相关字段的查询。
 * </p>
 * <p>
 * 此处理器会根据当前租户的 ID 动态生成租户条件，并在查询时加入该条件。
 * </p>
 * 
 * @author EdisonFeng
 * @date 2025/06/15
 */
@Slf4j
public class FengTenantHandler implements TenantLineHandler {

    private final FengTenantConfigProperties properties;
    private final Set<String> tenantTables; // 线程安全的表名集合


    /**
     * 构造函数
     * 通过依赖注入获取配置类，避免硬编码
     */
    public FengTenantHandler(FengTenantConfigProperties properties) {
        this.properties = properties;
        this.tenantTables = ConcurrentHashMap.newKeySet();
        this.tenantTables.addAll(properties.getTables());
    }
    
    /**
     * 获取租户 ID 值表达式，只支持单个 ID 值
     * <p>
     * 如果当前租户 ID 存在，则返回租户 ID；否则，返回 NullValue，表示没有租户 ID。
     * </p>
     * 
     * @return 租户 ID 值表达式
     */
    @Override
    public Expression getTenantId() {
    	String tenant = TenantContextHolder.getTenant();
        // 如果租户 ID 不存在，返回 NullValue
        if (StrUtil.isBlank(tenant)) {
            log.warn("租户 ID（tenantId）为空，无法拼接租户条件！");
            return new NullValue();
        }

        return new StringValue(tenant);
    }
    
    /**
     * 获取租户字段名
     * <p>
     * 返回从配置中获取的租户字段名，默认值为 "tenant_id"。
     * </p>
     * 
     * @return 租户字段名
     */
    @Override
    public String getTenantIdColumn() {
        return properties.getColumn();
    }

    /**
     * 根据表名判断是否忽略拼接多租户条件
     * <p>
     * 默认都要进行解析并拼接多租户条件，除非当前租户 ID 为空或表名在配置中被忽略。
     * </p>
     *
     * @param tableName 表名
     * @return 是否忽略，true:表示忽略，false:需要解析并拼接多租户条件
     */
    @Override
    public boolean ignoreTable(String tableName) {
        String tenant = TenantContextHolder.getTenant();

        // 租户ID为空，则默认过滤，以避免数据泄露（除非是租户表本身）
        if (StrUtil.isBlank(tenant)) {
            log.warn("租户信息为空，{}表不需要进行租户条件过滤！", tableName);
            return true;  
        }

        // 判断表名是否需要过滤
        boolean ignore = !tenantTables.contains(tableName);
        if (ignore) {
            log.warn("表 {} 不需要进行租户条件过滤", tableName);
        }
        return ignore;
    }

}
