package work.metanet.feng.ai.config;

import cn.hutool.core.util.StrUtil;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.DefaultMetadataStorageConfig;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import dev.langchain4j.store.embedding.redis.RedisEmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import work.metanet.feng.ai.api.entity.AigcEmbedStore;
import work.metanet.feng.ai.mapper.AigcEmbedStoreMapper;
import work.metanet.feng.common.core.constant.enums.EmbedStoreEnum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.PostConstruct;

/**
 * 向量库工厂类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Slf4j
@Component
public class EmbeddingStoreFactory {

    @Autowired
    private AigcEmbedStoreMapper aigcEmbedStoreMapper;

    private final List<AigcEmbedStore> modelStore = new ArrayList<>();
    private final Map<Integer, EmbeddingStore<TextSegment>> embedStoreMap = new ConcurrentHashMap<>();

    @Async
    @PostConstruct
    public void init() {
        modelStore.clear();
        List<AigcEmbedStore> list = aigcEmbedStoreMapper.selectList(Wrappers.<AigcEmbedStore>lambdaQuery());
        list.forEach(embed -> {
            try {
                if (StrUtil.equals(embed.getProvider(), EmbedStoreEnum.REDIS.getCode())) {
                    RedisEmbeddingStore.Builder builder = RedisEmbeddingStore.builder()
                            .host(embed.getHost())
                            .port(embed.getPort())
                            .indexName(embed.getDatabaseName())
                            .dimension(embed.getDimension());
                    if (StrUtil.isNotBlank(embed.getUsername()) && StrUtil.isNotBlank(embed.getPassword())) {
                        builder.user(embed.getUsername()).password(embed.getPassword());
                    } else if (StrUtil.isNotBlank(embed.getPassword())) {
                    	builder.password(embed.getPassword());
                    }
                    // 报错：Error creating bean with name 'embeddingStoreFactory': Invocation of init method failed; nested exception is java.lang.NoClassDefFoundError: redis/clients/jedis/JedisPooled
                    // 原因是：本项目已经创建了JedisPooled，所以langchain4j创建时会报错，解决 办法：使用其他类型的向量数据库：PGVECTOR MILVUS
                    EmbeddingStore<TextSegment> store = builder.build();
                    embedStoreMap.put(embed.getId(), store);
                }
                if (StrUtil.equals(embed.getProvider(), EmbedStoreEnum.PGVECTOR.getCode())) {
                    EmbeddingStore<TextSegment> store = PgVectorFullTextSearchStore.builder()
                            .dataSource(PgVectorFullTextSearchStore.createDataSource(embed.getHost(), embed.getPort(), embed.getUsername(), embed.getPassword(), embed.getDatabaseName()))
                            .tableName(embed.getTableName())
                            .dimension(embed.getDimension())
                            .indexListSize(100)
                            .useIndex(true)
                            .createTable(true)
                            .dropTableFirst(false)
                            .build();
                    embedStoreMap.put(embed.getId(), store);
                }
                if (StrUtil.equals(embed.getProvider(), EmbedStoreEnum.MILVUS.getCode())) {
                    EmbeddingStore<TextSegment> store = MilvusEmbeddingStore.builder()
                            .host(embed.getHost())
                            .port(embed.getPort())
                            .databaseName(embed.getDatabaseName())
                            .dimension(embed.getDimension())
                            .username(embed.getUsername())
                            .password(embed.getPassword())
                            .collectionName(embed.getTableName())
                            .build();
                    embedStoreMap.put(embed.getId(), store);
                }
                modelStore.add(embed);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("向量数据库初始化失败：[{}] --- [{}]，数据库配置信息：[{}]", embed.getName(), embed.getProvider(), embed);
            }
        });

        modelStore.forEach(i -> log.info("已成功注册Embedding Store：{}， 配置信息：{}", i.getProvider(), i));
    }

    public EmbeddingStore<TextSegment> getEmbeddingStore(Integer embeddingId) {
        return embedStoreMap.get(embeddingId);
    }

    public boolean containsEmbeddingStore(Integer embeddingId) {
        return embedStoreMap.containsKey(embeddingId);
    }
}
