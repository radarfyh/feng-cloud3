package ltd.huntinginfo.feng.ai.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.huntinginfo.feng.common.agent.component.EmbeddingRefreshEvent;
import ltd.huntinginfo.feng.common.agent.component.ProviderRefreshEvent;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 提供者事件侦听类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Slf4j
@Component
@AllArgsConstructor
public class ProviderListener {

    private final ModelStoreFactory providerInitialize;
    private final EmbeddingStoreFactory embeddingStoreInitialize;

    @EventListener
    public void providerEvent(ProviderRefreshEvent event) {
        log.info("refresh provider beans begin......");
        providerInitialize.init();
        log.info("refresh provider beans success......");
    }

    @EventListener
    public void providerEvent(EmbeddingRefreshEvent event) {
        log.info("refresh embedding beans begin......");
        embeddingStoreInitialize.init();
        log.info("refresh embedding beans success......");
    }
}
