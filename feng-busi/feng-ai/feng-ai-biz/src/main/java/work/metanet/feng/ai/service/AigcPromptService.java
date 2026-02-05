package work.metanet.feng.ai.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import work.metanet.feng.ai.api.entity.AigcPrompt;
import work.metanet.feng.common.core.util.R;

/**
 * AI提示语服务接口
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

public interface AigcPromptService extends IService<AigcPrompt> {

	List<AigcPrompt> list(AigcPrompt data);

	IPage<AigcPrompt> page(Page<AigcPrompt> page, AigcPrompt data);
	
}
