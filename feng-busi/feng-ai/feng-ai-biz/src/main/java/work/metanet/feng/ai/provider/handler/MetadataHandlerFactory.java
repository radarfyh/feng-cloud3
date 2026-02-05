package work.metanet.feng.ai.provider.handler;

import dev.langchain4j.store.embedding.pgvector.MetadataStorageConfig;


public class MetadataHandlerFactory {
    /**
     * Default Constructor
     */
    public MetadataHandlerFactory() {}
    /**
     * Retrieve the handler associated to the config
     * @param config MetadataConfig config
     * @return MetadataHandler
     */
    public static MetadataHandler get(MetadataStorageConfig config) {
        switch(config.storageMode()) {
            case COMBINED_JSON:
                return new JSONMetadataHandler(config);
            case COMBINED_JSONB:
                return new JSONBMetadataHandler(config);
            case COLUMN_PER_KEY:
                return new ColumnsMetadataHandler(config);
            default:
                throw new RuntimeException(String.format("Type %s not handled.", config.storageMode()));
        }
    }
}