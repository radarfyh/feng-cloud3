package work.metanet.feng.ai;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import lombok.extern.slf4j.Slf4j;
import work.metanet.feng.common.feign.annotation.EnableFengFeignClients;
import work.metanet.feng.common.security.annotation.EnableFengResourceServer;

/**
 * 项目启动类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */
@Slf4j
@EnableFengFeignClients
@EnableDiscoveryClient
@EnableFengResourceServer // 启用认证和权限必须注解
@SpringBootApplication
public class FengAIAgentApplication {
	//@SneakyThrows
	public static void main(String[] args) {
		SpringApplication app=new SpringApplication(FengAIAgentApplication.class);
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
