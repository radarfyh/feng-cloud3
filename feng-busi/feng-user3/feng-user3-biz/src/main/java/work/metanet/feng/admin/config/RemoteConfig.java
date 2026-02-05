package work.metanet.feng.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("system.config")
public class RemoteConfig {

    private boolean remoteDatasource;

    private int queryTimeout ;

    private int initialSize ;

    private int minIdle;

    private int maxActive;

}
