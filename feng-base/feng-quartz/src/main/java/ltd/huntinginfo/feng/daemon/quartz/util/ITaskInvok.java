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

package ltd.huntinginfo.feng.daemon.quartz.util;

import ltd.huntinginfo.feng.daemon.quartz.entity.SysJob;
import ltd.huntinginfo.feng.daemon.quartz.exception.TaskException;

/**
 * 定时任务反射实现接口
 *
 * @author lengleng
 * @date 2025/05/31
 */
public interface ITaskInvok {

	/**
	 * 执行反射方法
	 * @param sysJob 任务配置类
	 * @throws TaskException 执行任务时可能抛出的异常
	 */
	void invokMethod(SysJob sysJob) throws TaskException;

}
