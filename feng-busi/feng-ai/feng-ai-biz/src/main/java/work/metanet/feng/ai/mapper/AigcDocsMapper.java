package work.metanet.feng.ai.mapper;

import work.metanet.feng.ai.api.entity.AigcDocs;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI文档映射接口
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Mapper
public interface AigcDocsMapper extends BaseMapper<AigcDocs> {

}

