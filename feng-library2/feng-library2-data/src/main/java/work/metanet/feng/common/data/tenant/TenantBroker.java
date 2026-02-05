package work.metanet.feng.common.data.tenant;

import lombok.extern.slf4j.Slf4j;
import work.metanet.feng.common.core.config.TenantOfHeader;
import lombok.experimental.UtilityClass;

import java.util.function.Supplier;

import cn.hutool.core.util.StrUtil;

/**
 * 租户运行时代理，确保租户信息在业务方法执行时保持正确。
 * 通过此工具类可切换租户上下文，避免租户信息在嵌套调用时出现混乱。
 * 使用此类可避免直接操作 TenantContextHolder 可能带来的数据错乱。
 * 
 * @author EdisonFeng
 * @date 2025/06/15
 */
@Slf4j
@UtilityClass
public class TenantBroker {

    @FunctionalInterface
    public interface RunAs<T> {
        /**
         * 执行业务逻辑
         * 
         * @param tenantId 租户 ID
         * @throws Exception
         */
        void run(T tenantId) throws Exception;
    }

    @FunctionalInterface
    public interface ApplyAs<T, R> {
        /**
         * 执行业务逻辑，返回一个值
         * 
         * @param tenantId 租户 ID
         * @return 结果
         * @throws Exception
         */
        R apply(T tenantId) throws Exception;
    }

    /**
     * 以指定租户身份运行业务逻辑
     * <p>
     * 切换租户上下文并执行指定的业务逻辑，执行完成后恢复原租户上下文。
     * </p>
     *
     * @param tenantId 当前租户ID
     * @param func 执行的业务逻辑
     */
//    public void runAs(String tenantId, RunAs<String> func) {
//        if (StrUtil.isBlank(tenantId)) {
//        	throw new TenantBrokerExceptionWrapper("runAs传入的租户ID为空");
//        }
//        TenantOfHeader tenant = TenantContextHolder.getTenant();
//    	final String currentTenantId = tenant.getTenantId();
//        try {
//            log.info("runAs切换租户：{} -》 {}", currentTenantId, tenantId);
//            tenant.setTenantId(tenantId);
//            TenantContextHolder.setTenant(tenant);
//            func.run(tenantId);
//        } catch (Exception e) {
//            throw new TenantBrokerExceptionWrapper("runAs执行租户业务逻辑时发生错误", e);
//        } finally {
//            log.info("runAs恢复租户：{} -》 {}", tenantId, currentTenantId);
//            tenant.setTenantId(currentTenantId);
//            TenantContextHolder.setTenant(tenant);
//        }
//    }
    public void runAs(String tenantId, RunAs<String> func) {
        if (StrUtil.isBlank(tenantId)) {
        	throw new TenantBrokerExceptionWrapper("runAs传入的租户ID为空");
        }
        String tenant = TenantContextHolder.getTenant();
    	final String currentTenantId = tenant;
        try {
            log.debug("runAs切换租户：{} -》 {}", currentTenantId, tenantId);
            TenantContextHolder.setTenant(tenant);
            func.run(tenantId);
        } catch (Exception e) {
            throw new TenantBrokerExceptionWrapper("runAs执行租户业务逻辑时发生错误", e);
        } finally {
            log.debug("runAs恢复租户：{} -》 {}", tenantId, currentTenantId);
            TenantContextHolder.setTenant(tenant);
        }
    }

    /**
     * 以指定租户身份运行并返回结果
     * <p>
     * 切换租户上下文并执行指定的业务逻辑，执行完成后恢复原租户上下文，返回结果。
     * </p>
     *
     * @param tenantId 当前租户ID
     * @param func 执行的业务逻辑，返回结果
     * @param <T> 返回数据类型
     * @return 业务逻辑执行结果
     */
//    public <T> T applyAs(String tenantId, ApplyAs<String, T> func) {
//        if (StrUtil.isBlank(tenantId)) {
//        	throw new TenantBrokerExceptionWrapper("applyAs传入的租户ID为空");
//        }
//        TenantOfHeader tenant = TenantContextHolder.getTenant();
//        final String currentTenantId = tenant.getTenantId();
//        try {
//            log.info("applyAs切换租户：{} -》 {}", currentTenantId, tenantId);
//            tenant.setTenantId(tenantId);
//            TenantContextHolder.setTenant(tenant);
//            return func.apply(tenantId);
//        } catch (Exception e) {
//            throw new TenantBrokerExceptionWrapper("applyAs执行租户业务逻辑时发生错误", e);
//        } finally {
//            log.info("applyAs恢复租户：{} -》 {}", tenantId, currentTenantId);
//            tenant.setTenantId(currentTenantId);
//            TenantContextHolder.setTenant(tenant);
//        }
//    }
    public <T> T applyAs(String tenantId, ApplyAs<String, T> func) {
        if (StrUtil.isBlank(tenantId)) {
        	throw new TenantBrokerExceptionWrapper("applyAs传入的租户ID为空");
        }
        String tenant = TenantContextHolder.getTenant();
        final String currentTenantId = tenant;
        try {
            log.debug("applyAs切换租户：{} -》 {}", currentTenantId, tenantId);
            TenantContextHolder.setTenant(tenant);
            return func.apply(tenantId);
        } catch (Exception e) {
            throw new TenantBrokerExceptionWrapper("applyAs执行租户业务逻辑时发生错误", e);
        } finally {
            log.debug("applyAs恢复租户：{} -》 {}", tenantId, currentTenantId);
            TenantContextHolder.setTenant(tenant);
        }
    }

    /**
     * 以指定租户身份运行业务逻辑
     * 
     * @param supplier 提供租户ID的 Supplier
     * @param func 执行的业务逻辑
     */
    public void runAs(Supplier<String> supplier, RunAs<String> func) {
        runAs(supplier.get(), func);
    }

    /**
     * 以指定租户身份运行并返回结果
     * 
     * @param supplier 提供租户ID的 Supplier
     * @param func 执行的业务逻辑，返回结果
     * @param <T> 返回数据类型
     * @return 业务逻辑执行结果
     */
    public <T> T applyAs(Supplier<String> supplier, ApplyAs<String, T> func) {
        return applyAs(supplier.get(), func);
    }

    /**
     * 自定义异常包装类
     */
    public static class TenantBrokerExceptionWrapper extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public TenantBrokerExceptionWrapper(String message, Throwable cause) {
            super(message, cause);
        }

        public TenantBrokerExceptionWrapper(Throwable cause) {
            super(cause);
        }
        public TenantBrokerExceptionWrapper(String message) {
            super(message);
        }
    }
}
