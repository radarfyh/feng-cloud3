package work.metanet.feng.common.agent.component;

import org.springframework.context.ApplicationEvent;

/**
 * @author tycoding
 * @since 2024/6/16
 */
public class EmbeddingRefreshEvent extends ApplicationEvent {
    private static final long serialVersionUID = 4109980679877560773L;

    public EmbeddingRefreshEvent(Object source) {
        super(source);
    }
}
