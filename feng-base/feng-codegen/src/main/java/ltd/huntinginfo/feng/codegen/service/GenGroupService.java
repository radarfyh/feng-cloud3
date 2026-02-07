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

package ltd.huntinginfo.feng.codegen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import ltd.huntinginfo.feng.codegen.entity.GenGroupEntity;
import ltd.huntinginfo.feng.codegen.util.vo.GroupVO;
import ltd.huntinginfo.feng.codegen.util.vo.TemplateGroupDTO;

/**
 * 模板分组服务接口
 *
 * @author lengleng
 * @date 2025/05/31
 */
public interface GenGroupService extends IService<GenGroupEntity> {

	/**
	 * 保存生成模板组
	 * @param genTemplateGroup 模板组DTO对象
	 */
	void saveGenGroup(TemplateGroupDTO genTemplateGroup);

	/**
	 * 删除分组极其关系
	 * @param ids
	 */
	void delGroupAndTemplate(Long[] ids);

	/**
	 * 查询group数据
	 * @param id
	 */
	GroupVO getGroupVoById(Long id);

	/**
	 * 更新group数据
	 * @param GroupVo
	 */
	void updateGroupAndTemplateById(GroupVO GroupVo);

}
