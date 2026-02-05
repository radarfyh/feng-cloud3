package work.metanet.feng.ai.service.impl;

import work.metanet.feng.ai.api.entity.AigcEmbedStore;
import work.metanet.feng.ai.mapper.AigcEmbedStoreMapper;
import work.metanet.feng.ai.service.AigcEmbedStoreService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 向量库实现类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Service
@RequiredArgsConstructor
public class AigcEmbedStoreServiceImpl extends ServiceImpl<AigcEmbedStoreMapper, AigcEmbedStore> implements AigcEmbedStoreService {

}
