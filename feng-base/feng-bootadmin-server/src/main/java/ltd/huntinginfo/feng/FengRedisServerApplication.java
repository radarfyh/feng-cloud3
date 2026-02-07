package ltd.huntinginfo.feng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import ltd.huntinginfo.feng.config.EmbeddedRedisProperties;

@SpringBootApplication
@EnableAdminServer
@EnableDiscoveryClient
@EnableConfigurationProperties(EmbeddedRedisProperties.class)
public class FengRedisServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FengRedisServerApplication.class, args);
	}
}
