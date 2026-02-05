package work.metanet.feng.ai.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import work.metanet.feng.ai.api.entity.AigcApp;
import work.metanet.feng.ai.api.entity.AigcKnowledge;
import work.metanet.feng.ai.api.entity.AigcModel;
import work.metanet.feng.ai.mapper.AigcAppMapper;
import work.metanet.feng.ai.mapper.AigcKnowledgeMapper;
import work.metanet.feng.ai.mapper.AigcModelMapper;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import cn.hutool.core.bean.BeanUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;

/**
 * AI应用程序信息存储类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Slf4j
@Component
@AllArgsConstructor
public class AppStore {

    private static final Map<Integer, AigcApp> appMap = new HashMap<>();
    private final AigcAppMapper aigcAppMapper;
    private final AigcModelMapper aigcModelMapper;
    private final AigcKnowledgeMapper aigcKnowledgeMapper;
    
    @PostConstruct
    public void init() {
        log.info("initialize app config list...");
        List<AigcApp> list = aigcAppMapper.selectList(Wrappers.<AigcApp>lambdaQuery());
        list.forEach(app -> {
            AigcModel model = new AigcModel();
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

        	appMap.put(app.getId(), app);
        });
    }

    public AigcApp get(Integer appId) {
        return appMap.get(appId);
    }
}
