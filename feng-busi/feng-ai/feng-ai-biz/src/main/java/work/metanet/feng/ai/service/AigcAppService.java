package work.metanet.feng.ai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import work.metanet.feng.ai.api.entity.AigcApp;
import work.metanet.feng.common.core.util.R;

import java.util.List;

/**
 * 应用程序服务接口
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

public interface AigcAppService extends IService<AigcApp> {

    List<AigcApp> list(AigcApp data);

    AigcApp getById(Integer id);

	IPage<AigcApp> page(Page<AigcApp> page, AigcApp data);
}
