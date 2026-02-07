package ltd.huntinginfo.feng.ai.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import ltd.huntinginfo.feng.ai.api.entity.AigcAppApi;
import ltd.huntinginfo.feng.common.core.util.R;

/**
 * 渠道服务接口
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

public interface AigcAppApiService extends IService<AigcAppApi> {
	AigcAppApi getAppApiWithClassifiedPrompts(Integer id);

	AigcAppApi getByAppId(Integer appId);

	R<List<AigcAppApi>> list(AigcAppApi data);

	R<IPage<AigcAppApi>> page(Page<AigcAppApi> page, AigcAppApi data);

}
