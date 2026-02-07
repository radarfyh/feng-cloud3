/*
 *    Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of the developer nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * Author: lengleng
 */

package ltd.huntinginfo.feng.codegen;

import ltd.huntinginfo.feng.common.datasource.annotation.EnableDynamicDataSource;
import ltd.huntinginfo.feng.common.feign.annotation.EnableFengFeignClients;
import ltd.huntinginfo.feng.common.security.annotation.EnableFengResourceServer;
import ltd.huntinginfo.feng.common.swagger.annotation.EnableFengDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 代码生成模块应用启动类
 *
 * @author lengleng
 * @date 2025/05/31
 */
@EnableDynamicDataSource
@EnableFengFeignClients
@EnableFengDoc("gen")
@EnableDiscoveryClient
@EnableFengResourceServer
@SpringBootApplication
public class FengCodeGenApplication {

	public static void main(String[] args) {
		SpringApplication.run(FengCodeGenApplication.class, args);
	}

}
