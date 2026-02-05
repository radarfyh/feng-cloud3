package work.metanet.feng.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import work.metanet.feng.ai.api.entity.AigcApp;
import work.metanet.feng.ai.api.entity.AigcAppApi;
import work.metanet.feng.ai.api.entity.AigcKnowledge;
import work.metanet.feng.ai.api.entity.AigcModel;
import work.metanet.feng.ai.api.entity.AigcPrompt;
import work.metanet.feng.ai.mapper.AigcAppApiMapper;
import work.metanet.feng.ai.mapper.AigcAppMapper;
import work.metanet.feng.ai.mapper.AigcKnowledgeMapper;
import work.metanet.feng.ai.mapper.AigcModelMapper;
import work.metanet.feng.ai.mapper.AigcPromptMapper;
import work.metanet.feng.ai.service.AigcAppApiService;
import work.metanet.feng.common.core.util.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 渠道服务实现类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Service
@RequiredArgsConstructor
public class AigcAppApiServiceImpl extends ServiceImpl<AigcAppApiMapper, AigcAppApi> implements AigcAppApiService {

    private final AigcPromptMapper aigcPromptMapper;
    private final AigcAppMapper aigcAppMapper;
    private final AigcModelMapper aigcModelMapper;
    private final AigcKnowledgeMapper aigcKnowledgeMapper;

    @Override
    public R<List<AigcAppApi>> list(AigcAppApi data) {
        List<AigcAppApi> list = list(getWrapper(data));
        return R.ok(list.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));
    }

    @Override
    public R<IPage<AigcAppApi>> page(Page<AigcAppApi> page, AigcAppApi data) {
        IPage<AigcAppApi> iPage = page(page, getWrapper(data));

        return R.ok(iPage.convert(this::convertToVO));
    }
    
    Wrapper<AigcAppApi> getWrapper(AigcAppApi data) {
    	Wrapper<AigcAppApi> queryWrapper = Wrappers.<AigcAppApi>lambdaQuery()
	        .eq(data.getAppId() != null && data.getAppId() > 0, AigcAppApi::getAppId, data.getAppId())
	        .eq(data.getId() != null && data.getId() > 0, AigcAppApi::getId, data.getId())
	        .like(StrUtil.isNotBlank(data.getChannel()), AigcAppApi::getChannel, data.getChannel())
			.like(StrUtil.isNotBlank(data.getApiKey()), AigcAppApi::getApiKey, data.getApiKey())
			.like(StrUtil.isNotBlank(data.getSecretKey()), AigcAppApi::getSecretKey, data.getSecretKey())
	        .orderByDesc(AigcAppApi::getCreateTime);
    	return queryWrapper;
    }

    private AigcAppApi convertToVO(AigcAppApi appApi) {
    	AigcAppApi vo = new AigcAppApi();
        BeanUtil.copyProperties(appApi, vo);
        
        // 获取APP
        AigcModel model = new AigcModel();
        AigcApp app = aigcAppMapper.selectById(appApi.getAppId());
        if (app.getModelId() != null && app.getModelId() > 0) {
        	AigcModel ret = aigcModelMapper.selectById(app.getModelId());
        	BeanUtil.copyProperties(ret, model);
            app.setModel(model);
        }
        List<AigcKnowledge> knowledges = aigcKnowledgeMapper.selectList(Wrappers.<AigcKnowledge>lambdaQuery()
                .eq(AigcKnowledge::getAppId, app.getId()));
        app.setKnowledges(knowledges);
        List<Integer> knowledgeIds = new ArrayList<Integer>();
        knowledges.forEach(knowledge -> {
        	knowledgeIds.add(knowledge.getId());
        });
        app.setKnowledgeIds(knowledgeIds);
        vo.setApp(app);
        // 获取提示语
    	Wrapper<AigcPrompt> queryWrapper = Wrappers.<AigcPrompt>lambdaQuery()
    	        .eq(appApi.getId() != null && appApi.getId() > 0, AigcPrompt::getAppApiId, appApi.getId());        
        List<AigcPrompt> prompts = aigcPromptMapper.selectList(queryWrapper);
        vo.setPrompts(prompts);
        
        return vo;
    }
    
    @Override
    public AigcAppApi getAppApiWithClassifiedPrompts(Integer id) {
        AigcAppApi appApi = getById(id);
        if (appApi == null) {
            return null;
        }

        List<AigcPrompt> prompts = aigcPromptMapper.selectList(Wrappers.<AigcPrompt>lambdaQuery()
                .eq(AigcPrompt::getAppApiId, id));

        appApi.setPrompts(prompts);

        return appApi;
    }

    @Override
    public AigcAppApi getByAppId(Integer appId) {
        AigcAppApi appApi = getOne(Wrappers.<AigcAppApi>lambdaQuery()
                .eq(AigcAppApi::getAppId, appId)
                .last("limit 1"));

        if (appApi == null) {
            return null;
        }

        // 加载所有分类提示语
        List<AigcPrompt> prompts = aigcPromptMapper.selectList(Wrappers.<AigcPrompt>lambdaQuery()
                .eq(AigcPrompt::getAppApiId, appApi.getId()));

        appApi.setPrompts(prompts);
        
        return appApi;
    }
}
