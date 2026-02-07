package ltd.huntinginfo.feng.ai.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import ltd.huntinginfo.feng.ai.api.entity.AigcPrompt;

/**
 * AI提示语映射接口
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Mapper
public interface AigcPromptMapper extends BaseMapper<AigcPrompt> {
	
}
