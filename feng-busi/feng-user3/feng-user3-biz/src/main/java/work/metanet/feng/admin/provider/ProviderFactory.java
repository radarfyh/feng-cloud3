package work.metanet.feng.admin.provider;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import work.metanet.feng.admin.api.constants.DatasourceTypes;
import work.metanet.feng.admin.api.dto.DataSourceType;

@Configuration
public class ProviderFactory implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(final ApplicationContext ctx) {
        this.context =  ctx;
        for(final DatasourceTypes d: DatasourceTypes.values()) {
            final ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) context).getBeanFactory();
            if(d.isDatasource()){
                DataSourceType dataSourceType = new DataSourceType(d.getType(), d.getName(), false, d.getExtraParams(), d.isJdbc());
                if(dataSourceType.getType().equalsIgnoreCase("oracle")){
                    dataSourceType.setCharset(d.getCharset());
                    dataSourceType.setTargetCharset(d.getTargetCharset());
                }
                dataSourceType.setKeywordSuffix(d.getKeywordSuffix());
                dataSourceType.setKeywordPrefix(d.getKeywordPrefix());
                dataSourceType.setAliasSuffix(d.getAliasSuffix());
                dataSourceType.setAliasPrefix(d.getAliasPrefix());
                beanFactory.registerSingleton(d.getType(), dataSourceType);
            }
        }
    }

    public static Provider getProvider() {
           return context.getBean("jdbc", Provider.class);
    }

    public static QueryProvider getQueryProvider(String type) {
        switch (type) {
            case "mysql":
            case "mariadb":
                return context.getBean("mysqlQueryProvider", QueryProvider.class);
            default:
                return context.getBean(type + "QueryProvider", QueryProvider.class);
        }

    }

}
