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

package ltd.huntinginfo.feng.daemon.quartz.config;

import org.quartz.Scheduler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import ltd.huntinginfo.feng.daemon.quartz.constants.FengQuartzEnum;
import ltd.huntinginfo.feng.daemon.quartz.service.SysJobService;
import ltd.huntinginfo.feng.daemon.quartz.util.TaskUtil;

import lombok.AllArgsConstructor;

/**
 * 初始化加载定时任务配置类
 *
 * @author lengleng
 * @author 郑健楠
 * @date 2025/05/31
 */
@Configuration
@AllArgsConstructor
public class FengInitQuartzJob implements InitializingBean {

	private final SysJobService sysJobService;

	private final TaskUtil taskUtil;

	private final Scheduler scheduler;

	/**
	 * 在属性设置完成后执行，根据任务状态进行相应操作
	 * @throws Exception 执行过程中可能抛出的异常
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		sysJobService.list().forEach(sysjob -> {
			if (FengQuartzEnum.JOB_STATUS_RELEASE.getType().equals(sysjob.getJobStatus())) {
				taskUtil.removeJob(sysjob, scheduler);
			}
			else if (FengQuartzEnum.JOB_STATUS_RUNNING.getType().equals(sysjob.getJobStatus())) {
				taskUtil.resumeJob(sysjob, scheduler);
			}
			else if (FengQuartzEnum.JOB_STATUS_NOT_RUNNING.getType().equals(sysjob.getJobStatus())) {
				taskUtil.pauseJob(sysjob, scheduler);
			}
			else {
				taskUtil.removeJob(sysjob, scheduler);
			}
		});
	}

}
