package work.metanet.feng.common.data.mybatis;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import work.metanet.feng.admin.api.feign.RemoteDataScopeService;
import work.metanet.feng.common.data.config.FengMybatisProperties;
import work.metanet.feng.common.data.datascope.DataScopeInnerInterceptor;
import work.metanet.feng.common.data.datascope.DataScopeInterceptor;
import work.metanet.feng.common.data.datascope.DataScopeSqlInjector;
import work.metanet.feng.common.data.datascope.FengDefaultDatascopeHandle;
import work.metanet.feng.common.data.resolver.SqlFilterArgumentResolver;
import work.metanet.feng.common.data.tenant.FengTenantConfigProperties;
import work.metanet.feng.common.data.tenant.FengTenantHandler;
import work.metanet.feng.common.data.tenant.TenantContextFilter;
import work.metanet.feng.common.security.component.PermissionService;
import work.metanet.feng.common.security.service.FengUser;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.util.List;
import java.util.Properties;

/**
 * MybatisPlus 配置类，包含了多租户、数据权限、SQL 日志等功能的集成配置。
 * <p>
 * 主要配置了数据权限拦截器、租户处理器以及 SQL 格式化输出等功能。
 * </p>
 */
@Configuration
@ConditionalOnBean(DataSource.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@EnableConfigurationProperties(FengMybatisProperties.class)
public class MybatisPlusConfiguration implements WebMvcConfigurer {

    /**
     * 增加请求参数解析器，对请求中的参数注入 SQL 检查
     *
     * @param resolverList 请求参数解析器列表
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolverList) {
        resolverList.add(new SqlFilterArgumentResolver());
    }

    /**
     * 配置 Mybatis Plus 拦截器，启用多租户、数据权限及分页支持
     *
     * @param tenantLineInnerInterceptor 租户拦截器
     * @param dataScopeInterceptor 数据权限拦截器
     * @return 配置好的 MybatisPlusInterceptor 实例
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(TenantLineInnerInterceptor tenantLineInnerInterceptor,
                                                         DataScopeInterceptor dataScopeInterceptor) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 注入多租户支持
        interceptor.addInnerInterceptor(tenantLineInnerInterceptor);
        // 数据权限
        interceptor.addInnerInterceptor(dataScopeInterceptor);
        // 分页支持 在work.metanet.feng.common.mybatis.FengPageConfiguration已经支持分页，故注释之 20250301
        // PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        // paginationInnerInterceptor.setMaxLimit(1000L); // 设置分页最大记录数
        // interceptor.addInnerInterceptor(paginationInnerInterceptor);
        return interceptor;
    }

    /**
     * 创建租户维护处理器对象
     *
     * @param tenantConfigProperties 租户配置属性
     * @param permissionService 权限服务
     * @return 租户处理器
     */
    @Bean
    @ConditionalOnMissingBean
    public TenantLineInnerInterceptor tenantLineInnerInterceptor(FengTenantConfigProperties tenantConfigProperties) {
        TenantLineInnerInterceptor tenantLineInnerInterceptor = new TenantLineInnerInterceptor();
        tenantLineInnerInterceptor.setTenantLineHandler(new FengTenantHandler(tenantConfigProperties));
        return tenantLineInnerInterceptor;
    }
    
    @Bean
    public FilterRegistrationBean<TenantContextFilter> tenantFilterRegistration(TenantContextFilter filter) {
        FilterRegistrationBean<TenantContextFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.setOrder(-200); // 比 Security 早执行
        return registration;
    }
    
    /**
     * 数据权限拦截器
     *
     * @param dataScopeService 数据权限服务
     * @return 数据权限拦截器实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(FengUser.class)
    public DataScopeInterceptor dataScopeInterceptor(RemoteDataScopeService dataScopeService) {
        DataScopeInnerInterceptor dataScopeInnerInterceptor = new DataScopeInnerInterceptor();
        dataScopeInnerInterceptor.setDataScopeHandle(new FengDefaultDatascopeHandle(dataScopeService));
        return dataScopeInnerInterceptor;
    }

    /**
     * 扩展 Mybatis-Plus baseMapper 支持数据权限
     *
     * @return 数据权限 SQL 注入器
     */
    @Bean
    @ConditionalOnBean(DataScopeInterceptor.class)
    public DataScopeSqlInjector dataScopeSqlInjector() {
        return new DataScopeSqlInjector();
    }

    /**
     * SQL 日志格式化
     *
     * @param properties Mybatis 配置属性
     * @return Druid SQL 日志过滤器
     */
    @Bean
    public DruidSqlLogFilter sqlLogFilter(FengMybatisProperties properties) {
        return new DruidSqlLogFilter(properties);
    }

    /**
     * 审计字段自动填充
     *
     * @return MybatisPlusMetaObjectHandler 实例
     */
    @Bean
    public MybatisPlusMetaObjectHandler mybatisPlusMetaObjectHandler() {
        return new MybatisPlusMetaObjectHandler();
    }

    /**
     * 数据库方言配置
     *
     * @return 数据库方言提供器
     */
    @Bean
    public DatabaseIdProvider databaseIdProvider() {
        VendorDatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        Properties properties = new Properties();
        properties.setProperty("SQL Server", "mssql");
        databaseIdProvider.setProperties(properties);
        return databaseIdProvider;
    }

}
