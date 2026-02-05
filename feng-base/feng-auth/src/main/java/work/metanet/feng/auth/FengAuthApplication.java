package work.metanet.feng.auth;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import work.metanet.feng.common.feign.annotation.EnableFengFeignClients;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 认证授权中心
 */
@Slf4j
@EnableFengFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {"work.metanet.feng"})
public class FengAuthApplication {

	//@SneakyThrows
	public static void main(String[] args) {
		SpringApplication app=new SpringApplication(FengAuthApplication.class);
		ConfigurableApplicationContext application = app.run(args);
		Environment env = application.getEnvironment();
		try {
			log.info("\n----------------------------------------------------------\n\t" +
							"Application '{}' is running! Access URLs:\n\t" +
							"Local: \t\thttp://localhost:{}\n\t" +
							"External: \thttp://{}:{}\n\t"+
							"Doc: \thttp://{}:{}/doc.html\n"+
							"----------------------------------------------------------",
					env.getProperty("spring.application.name"),
					env.getProperty("server.port"),
					InetAddress.getLocalHost().getHostAddress(),
					env.getProperty("server.port"),
					InetAddress.getLocalHost().getHostAddress(),
					env.getProperty("server.port"));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
