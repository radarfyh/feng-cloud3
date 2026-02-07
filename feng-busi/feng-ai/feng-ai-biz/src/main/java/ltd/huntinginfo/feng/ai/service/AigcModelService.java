package ltd.huntinginfo.feng.ai.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import ltd.huntinginfo.feng.ai.api.entity.AigcModel;
import ltd.huntinginfo.feng.common.core.util.QueryPage;

import java.util.List;

/**
 * AI模型服务接口
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

public interface AigcModelService extends IService<AigcModel> {

    List<AigcModel> getChatModels();

    List<AigcModel> getImageModels();

    List<AigcModel> getEmbeddingModels();

    List<AigcModel> list(AigcModel data);

    Page<AigcModel> page(AigcModel data, Page<AigcModel> page);

    AigcModel selectById(Integer id);
}

