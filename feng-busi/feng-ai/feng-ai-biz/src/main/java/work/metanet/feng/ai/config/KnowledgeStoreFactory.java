package work.metanet.feng.ai.config;

import work.metanet.feng.ai.api.entity.AigcEmbedStore;
import work.metanet.feng.ai.api.entity.AigcKnowledge;
import work.metanet.feng.ai.api.entity.AigcModel;
import work.metanet.feng.ai.mapper.AigcEmbedStoreMapper;
import work.metanet.feng.ai.mapper.AigcKnowledgeMapper;
import work.metanet.feng.ai.mapper.AigcModelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

/**
 * 知识库工厂类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Slf4j
@Component
public class KnowledgeStoreFactory {

    @Autowired
    private AigcKnowledgeMapper knowledgeMapper;
    @Autowired
    private AigcModelMapper modelMapper;
    @Autowired
    private AigcEmbedStoreMapper embedStoreMapper;

    private final Map<Integer, AigcKnowledge> knowledgeMap = new ConcurrentHashMap<>();

    @Async
    @PostConstruct
    public void init() {
        knowledgeMap.clear();
        List<AigcKnowledge> list = knowledgeMapper.selectList(Wrappers.<AigcKnowledge>lambdaQuery());
        Map<Integer, List<AigcModel>> modelMap = modelMapper.selectList(Wrappers.<AigcModel>lambdaQuery())
        		.stream().collect(Collectors.groupingBy(AigcModel::getId));
        Map<Integer, List<AigcEmbedStore>> storeMap = embedStoreMapper.selectList(Wrappers.<AigcEmbedStore>lambdaQuery())
        		.stream().collect(Collectors.groupingBy(AigcEmbedStore::getId));
        list.forEach(know -> {
            if (know.getEmbedModelId() != null) {
                List<AigcModel> models = modelMap.get(know.getEmbedModelId());
                know.setEmbedModel(models == null ? null : models.get(0));
            }
            if (know.getEmbedStoreId() != null) {
                List<AigcEmbedStore> stores = storeMap.get(know.getEmbedStoreId());
                know.setEmbedStore(stores == null ? null : stores.get(0));
            }
            knowledgeMap.put(know.getId(), know);
        });
    }

    public AigcKnowledge getKnowledge(Integer knowledgeId) {
        return knowledgeMap.get(knowledgeId);
    }

    public boolean containsKnowledge(Integer knowledgeId) {
        return knowledgeMap.containsKey(knowledgeId);
    }
}
