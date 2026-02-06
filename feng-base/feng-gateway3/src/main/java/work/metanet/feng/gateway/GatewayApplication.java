package work.metanet.feng.gateway;

import work.metanet.feng.common.gateway.annotation.EnableFengDynamicRoute;

import java.net.InetAddress;
import java.net.UnknownHostException;

//import work.metanet.feng.common.swagger.annotation.EnableFengSwagger3;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableFengDynamicRoute
@EnableDiscoveryClient
@SpringBootApplication
public class GatewayApplication {

	//@SneakyThrows
	public static void main(String[] args) {
		SpringApplication app=new SpringApplication(GatewayApplication.class);
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
