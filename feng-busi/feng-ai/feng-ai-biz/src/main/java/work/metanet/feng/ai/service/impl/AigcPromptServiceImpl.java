package work.metanet.feng.ai.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import work.metanet.feng.ai.api.entity.AigcPrompt;
import work.metanet.feng.ai.mapper.AigcPromptMapper;
import work.metanet.feng.ai.service.AigcPromptService;

/**
 * 提示语服务实现类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Service
@RequiredArgsConstructor
public class AigcPromptServiceImpl extends ServiceImpl<AigcPromptMapper, AigcPrompt> implements AigcPromptService {
	
	@Override
	public List<AigcPrompt> list(AigcPrompt data) {
	    List<AigcPrompt> list = baseMapper.selectList(getWrapper(data));
	
	    return list;
    }

	@Override
    public IPage<AigcPrompt> page(Page<AigcPrompt> page, AigcPrompt data) {
        IPage<AigcPrompt> iPage = baseMapper.selectPage(page, getWrapper(data));

        return iPage;
    }
	
    Wrapper<AigcPrompt> getWrapper(AigcPrompt data) {
    	Wrapper<AigcPrompt> queryWrapper = Wrappers.<AigcPrompt>lambdaQuery()
        		.like(StrUtil.isNotBlank(data.getName()), AigcPrompt::getName, data.getName())
        		.like(StrUtil.isNotBlank(data.getContent()), AigcPrompt::getContent, data.getContent())
        		.eq(data.getType() != null, AigcPrompt::getType, data.getType())
        		.eq(data.getId() != null, AigcPrompt::getId, data.getId())
        		.eq(data.getAppApiId() != null, AigcPrompt::getAppApiId, data.getAppApiId())
        		.orderByDesc(AigcPrompt::getCreateTime);
    	return queryWrapper;
    }
}
