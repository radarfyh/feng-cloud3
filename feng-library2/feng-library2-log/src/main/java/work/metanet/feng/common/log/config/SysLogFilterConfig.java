package work.metanet.feng.common.log.config;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;
import work.metanet.feng.common.log.filter.CacheRequestBodyFilter;


@Configuration
@Slf4j
public class SysLogFilterConfig implements ServletContextInitializer {
    @Override
    public void onStartup(ServletContext servletContext) {
        FilterRegistration.Dynamic registration = servletContext.addFilter(
            "cacheRequestBodyFilter", 
            new CacheRequestBodyFilter()
        );
        registration.addMappingForUrlPatterns(
            EnumSet.of(DispatcherType.REQUEST), 
            true, 
            "/*"
        );

        log.info("onStartup-->手动注册过滤器成功！！！");
    }
}
