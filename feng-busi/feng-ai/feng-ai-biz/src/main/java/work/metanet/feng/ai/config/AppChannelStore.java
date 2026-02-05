package work.metanet.feng.ai.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import work.metanet.feng.common.core.util.ServletUtil;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.core.bean.BeanUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;

/**
 * 应用程序渠道存储类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Slf4j
@Component
@AllArgsConstructor
public class AppChannelStore {

    private final Map<String, AigcAppApi> API_MAP = new HashMap<>();

    private final AigcAppApiMapper appApiMapper;
    private final AigcPromptMapper aigcPromptMapper;
    private final AigcAppMapper aigcAppMapper;
    private final AigcModelMapper aigcModelMapper;
    private final AigcKnowledgeMapper aigcKnowledgeMapper;
    
    public AigcAppApi getApiChannel() {
        String apiKey = ServletUtil.getHeader("X-API-KEY");
        if (apiKey != null && !apiKey.isEmpty()) {
            AigcAppApi api = API_MAP.get(apiKey);
            if (api != null) {
                return api;
            }
        }
        return null;
    }

    @PostConstruct
    public void init() {
        log.info("initialize app channel config list...");
        List<AigcAppApi> appApis = appApiMapper.selectList(Wrappers.<AigcAppApi>lambdaQuery());
        appApis.forEach(appApi -> {
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
            appApi.setApp(app);
            // 获取提示语
        	Wrapper<AigcPrompt> queryWrapper = Wrappers.<AigcPrompt>lambdaQuery()
        	        .eq(appApi.getId() != null && appApi.getId() > 0, AigcPrompt::getAppApiId, appApi.getId());        
            List<AigcPrompt> prompts = aigcPromptMapper.selectList(queryWrapper);
            appApi.setPrompts(prompts);
            
            API_MAP.put(appApi.getApiKey(), appApi);
        });
    }

    public Boolean isExpired() {
    	String apiKey = ServletUtil.getHeader("X-API-KEY");
    	if (apiKey != null && !apiKey.isEmpty()) {
    		AigcAppApi data = API_MAP.get(apiKey);
    		LocalDateTime nowLDT = LocalDateTime.now();
    		Duration duration = Duration.between(data.getUpdateTime(), nowLDT);
    		long daysBetween = duration.toDays();
    		if (daysBetween <= 2) {
    			return Boolean.FALSE;
    		}
    	}
		return Boolean.TRUE;
    }
}
