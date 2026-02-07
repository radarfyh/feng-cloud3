package ltd.huntinginfo.feng.daemon.quartz;

import ltd.huntinginfo.feng.common.feign.annotation.EnableFengFeignClients;
import ltd.huntinginfo.feng.common.security.annotation.EnableFengResourceServer;
import ltd.huntinginfo.feng.common.swagger.annotation.EnableFengDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * FengQuartz应用启动类
 * <p>
 * 集成定时任务、Feign客户端、资源服务及服务发现功能
 *
 * @author lengleng
 * @author frwcloud
 * @date 2025/05/31
 */
@EnableFengDoc("job")
@EnableFengFeignClients
@EnableFengResourceServer
@EnableDiscoveryClient
@SpringBootApplication
public class FengQuartzApplication {

	public static void main(String[] args) {
		SpringApplication.run(FengQuartzApplication.class, args);
	}

}
