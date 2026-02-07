package ltd.huntinginfo.feng.ai.config;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.*;
import dev.langchain4j.store.embedding.pgvector.DefaultMetadataStorageConfig;
import dev.langchain4j.store.embedding.pgvector.MetadataStorageConfig;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import ltd.huntinginfo.feng.ai.provider.handler.MetadataHandler;
import ltd.huntinginfo.feng.ai.provider.handler.MetadataHandlerFactory;

import javax.sql.DataSource;

import org.postgresql.ds.PGSimpleDataSource;

import com.pgvector.PGvector;

import static dev.langchain4j.internal.Utils.getOrDefault;
import static dev.langchain4j.internal.ValidationUtils.ensureGreaterThanZero;
import static dev.langchain4j.internal.ValidationUtils.ensureNotBlank;
import static dev.langchain4j.internal.ValidationUtils.ensureNotNull;

import java.sql.*;
import java.util.*;

@Slf4j
public class PgVectorFullTextSearchStore implements EmbeddingStore<TextSegment> {
    private static final String DEFAULT_TABLE_NAME = "embeddings";
    private static final String TEXT_SEARCH_COLUMN = "text_search";
    
    private final DataSource dataSource;
    private final String tableName;
    private final int dimension;
    private final MetadataHandler metadataHandler;
    private final Boolean useIndex;
    private final Integer indexListSize;
    private final Boolean createTable;
    private final Boolean dropTableFirst;

   
    public static DataSource createDataSource(String host, Integer port, String user, String password, String database) {
        host = ensureNotBlank(host, "host");
        port = ensureGreaterThanZero(port, "port");
        user = ensureNotBlank(user, "user");
        password = ensureNotBlank(password, "password");
        database = ensureNotBlank(database, "database");

        PGSimpleDataSource source = new PGSimpleDataSource();
        source.setServerNames(new String[]{host});
        source.setPortNumbers(new int[]{port});
        source.setDatabaseName(database);
        source.setUser(user);
        source.setPassword(password);

        return source;
    }
    
    /**
     * Constructor for PgVectorFullTextSearchStore Class
     *
     * @param datasource            The datasource to use
     * @param table                 The database table
     * @param dimension             The vector dimension
     * @param useIndex              Should use <a href="https://github.com/pgvector/pgvector#ivfflat">IVFFlat</a> index
     * @param indexListSize         The IVFFlat number of lists
     * @param createTable           Should create table automatically
     * @param dropTableFirst        Should drop table first, usually for testing
     * @param metadataStorageConfig The {@link MetadataStorageConfig} config.
     */
    @Builder
    public PgVectorFullTextSearchStore(DataSource dataSource, 
                                     String tableName, 
                                     int dimension,
                                     MetadataStorageConfig metadataConfig,
                                     Boolean useIndex,
                                     Integer indexListSize,
                                     Boolean createTable,
                                     Boolean dropTableFirst) {
        this.dataSource = Objects.requireNonNull(dataSource);
        this.tableName = tableName != null ? tableName : DEFAULT_TABLE_NAME;
        this.dimension = dimension;
        MetadataStorageConfig config = getOrDefault(metadataConfig, DefaultMetadataStorageConfig.defaultConfig());
        this.metadataHandler = MetadataHandlerFactory.get(config);
        this.useIndex = getOrDefault(useIndex, false);
        this.createTable = getOrDefault(createTable, true);
        this.dropTableFirst = getOrDefault(dropTableFirst, false);  
        this.indexListSize = indexListSize;
        initTable4FullText();
    }
    
    private void initTable4FullText() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            if (dropTableFirst) {
            	stmt.executeUpdate(String.format("DROP TABLE IF EXISTS %s", tableName));
            }
            // 创建主表
            if (createTable) {
	            String sql = String.format(
	                "CREATE TABLE IF NOT EXISTS %s (" +
	                "embedding_id UUID PRIMARY KEY, " +
	                "embedding VECTOR(%d), " +
	                "text TEXT, " +
	                "%s, " +
	                "%s TSVECTOR)", 
	                tableName, dimension, 
	                metadataHandler.columnDefinitionsString(),
	                TEXT_SEARCH_COLUMN);
	            
	            stmt.executeUpdate(sql);
            }
            if (useIndex) {
	            // 创建向量索引

	            stmt.executeUpdate(String.format(
	                "CREATE INDEX IF NOT EXISTS %s_embedding_idx ON %s USING IVFFLAT (embedding) WITH (lists = %s)",
	                tableName, tableName, ensureGreaterThanZero(indexListSize, "indexListSize")));
	                
	            // 创建全文搜索索引
	            stmt.executeUpdate(String.format(
	                "CREATE INDEX IF NOT EXISTS %s_text_search_idx ON %s USING GIN(%s)",
	                tableName, tableName, TEXT_SEARCH_COLUMN));
	                
	            // 创建元数据索引
	            metadataHandler.createMetadataIndexes(stmt, tableName);
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize table", e);
        }
    }
    
    // 获取数据库连接（带PGvector初始化）
    private Connection getConnection() throws SQLException {
        Connection conn = dataSource.getConnection();
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE EXTENSION IF NOT EXISTS vector");
            PGvector.addVectorType(conn);
        }
        return conn;
    }
    
    @Override
    public String add(Embedding embedding) {
        String id = UUID.randomUUID().toString();
        add(id, embedding, null);
        return id;
    }

    @Override
    public void add(String id, Embedding embedding) {
        add(id, embedding, null);
    }

    @Override
    public String add(Embedding embedding, TextSegment textSegment) {
        String id = UUID.randomUUID().toString();
        add(id, embedding, textSegment);
        return id;
    }

    private void add(String id, Embedding embedding, TextSegment textSegment) {
        String sql = String.format(
            "INSERT INTO %s (embedding_id, embedding, text, %s, %s) " +
            "VALUES (?, ?, ?, %s, to_tsvector('english', ?)) " +
            "ON CONFLICT (embedding_id) DO UPDATE SET " +
            "embedding = EXCLUDED.embedding, " +
            "text = EXCLUDED.text, " +
            "%s = EXCLUDED.%s, " +
            "%s = to_tsvector('english', EXCLUDED.text)",
            tableName, 
            String.join(",", metadataHandler.columnsNames()),
            TEXT_SEARCH_COLUMN,
            String.join(",", Collections.nCopies(metadataHandler.columnsNames().size(), "?")),
            String.join(",", metadataHandler.columnsNames()),
            String.join(",", metadataHandler.columnsNames()),
            TEXT_SEARCH_COLUMN);

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setObject(1, UUID.fromString(id));
            stmt.setObject(2, new PGvector(embedding.vector()));
            
            if (textSegment != null) {
                stmt.setString(3, textSegment.text());
                metadataHandler.setMetadata(stmt, 4, textSegment.metadata());
                stmt.setString(4 + metadataHandler.columnsNames().size(), textSegment.text());
            } else {
                stmt.setNull(3, Types.VARCHAR);
                for (int i = 0; i < metadataHandler.columnsNames().size(); i++) {
                    stmt.setNull(4 + i, Types.OTHER);
                }
                stmt.setNull(4 + metadataHandler.columnsNames().size(), Types.VARCHAR);
            }
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add embedding", e);
        }
    }

    /**
     * 执行全文搜索
     * @param query 搜索查询
     * @param limit 最大结果数
     * @param minScore 最小相关度分数(0-1)
     * @return 匹配的文本片段列表
     */
    public List<TextSegment> fullTextSearch(String query, int limit, double minScore) {
        List<TextSegment> results = new ArrayList<>();
        
        String sql = String.format(
            "SELECT embedding_id, text, ts_rank(%s, plainto_tsquery('simple', ?)) as score, %s " +
            "FROM %s " +
            "WHERE %s @@ plainto_tsquery('simple', ?) AND " +
            "ts_rank(%s, plainto_tsquery('simple', ?)) >= ? " +
            "ORDER BY score DESC " +
            "LIMIT ?",
            TEXT_SEARCH_COLUMN,
            String.join(",", metadataHandler.columnsNames()),
            tableName,
            TEXT_SEARCH_COLUMN,
            TEXT_SEARCH_COLUMN);
        
        // 打印实际执行的SQL和参数
        log.debug("Executing SQL: {}", sql);
        log.debug("Parameters: query='{}', minScore={}, limit={}", query, minScore, limit);

        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, query);
            stmt.setString(2, query);
            stmt.setString(3, query);
            stmt.setDouble(4, minScore);
            stmt.setInt(5, limit);
            
            // 打印最终SQL（需依赖JDBC驱动支持）
            log.debug("Final SQL with params: {}", stmt.toString());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("embedding_id");
                    String text = rs.getString("text");
                    double score = rs.getDouble("score");
                    
                    Metadata metadata = metadataHandler.fromResultSet(rs);
                    TextSegment segment = TextSegment.from(text, metadata);
                    segment.metadata().add("search_score", String.valueOf(score));
                    
                    log.debug("Found match: id={}, text={}, score={}", id, text, score);
                    
                    results.add(segment);
                }
            }
        } catch (SQLException e) {
            log.error("Full-text search failed. SQL: {}, Params: {}", sql, 
                    new Object[]{query, query, query, minScore, limit}, e);
            
            throw new RuntimeException("Full-text search failed", e);
        }
        
        return results;
    }
    
    /**
     * 混合搜索 - 结合向量相似度和全文搜索
     * @param embeddingQuery 向量查询
     * @param textQuery 文本查询
     * @param limit 最大结果数
     * @param vectorWeight 向量权重(0-1)
     * @param minScore 最小综合分数
     */
    public List<TextSegment> hybridSearch(Embedding embeddingQuery, 
                                        String textQuery, 
                                        int limit, 
                                        double vectorWeight,
                                        double minScore) {
        List<TextSegment> results = new ArrayList<>();
        
        String sql = String.format(
            "SELECT e.embedding_id, e.text, " +
            "(2 - (e.embedding <=> ?)) / 2 AS vector_score, " +
            "ts_rank(e.%s, plainto_tsquery('english', ?)) AS text_score, " +
            "((2 - (e.embedding <=> ?)) / 2 * ? + " +
            "ts_rank(e.%s, plainto_tsquery('english', ?)) * ?) AS combined_score, " +
            "%s " +
            "FROM %s e " +
            "WHERE e.%s @@ plainto_tsquery('english', ?) " +
            "AND ((2 - (e.embedding <=> ?)) / 2 * ? + " +
            "ts_rank(e.%s, plainto_tsquery('english', ?)) * ?) >= ? " +
            "ORDER BY combined_score DESC " +
            "LIMIT ?",
            TEXT_SEARCH_COLUMN,
            TEXT_SEARCH_COLUMN,
            String.join(",", metadataHandler.columnsNames()),
            tableName,
            TEXT_SEARCH_COLUMN,
            TEXT_SEARCH_COLUMN);

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            PGvector vector = new PGvector(embeddingQuery.vector());
            double textWeight = 1.0 - vectorWeight;
            
            stmt.setObject(1, vector);
            stmt.setString(2, textQuery);
            stmt.setObject(3, vector);
            stmt.setDouble(4, vectorWeight);
            stmt.setString(5, textQuery);
            stmt.setDouble(6, textWeight);
            stmt.setString(7, textQuery);
            stmt.setObject(8, vector);
            stmt.setDouble(9, vectorWeight);
            stmt.setString(10, textQuery);
            stmt.setDouble(11, textWeight);
            stmt.setDouble(12, minScore);
            stmt.setInt(13, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("embedding_id");
                    String text = rs.getString("text");
                    double vectorScore = rs.getDouble("vector_score");
                    double textScore = rs.getDouble("text_score");
                    double combinedScore = rs.getDouble("combined_score");
                    
                    Metadata metadata = metadataHandler.fromResultSet(rs);
                    metadata.add("vector_score", String.valueOf(vectorScore));
                    metadata.add("text_score", String.valueOf(textScore));
                    metadata.add("combined_score", String.valueOf(combinedScore));
                    
                    results.add(TextSegment.from(text, metadata));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Hybrid search failed", e);
        }
        
        return results;
    }
    
    @Override
    public List<String> addAll(List<Embedding> embeddings) {
        List<String> ids = new ArrayList<>();
        for (Embedding embedding : embeddings) {
            ids.add(add(embedding));
        }
        return ids;
    }

    @Override
    public List<String> addAll(List<Embedding> embeddings, List<TextSegment> embedded) {
        if (embeddings.size() != embedded.size()) {
            throw new IllegalArgumentException("Embeddings and embedded lists must have same size");
        }
        
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < embeddings.size(); i++) {
            ids.add(add(embeddings.get(i), embedded.get(i)));
        }
        return ids;
    }

    @Override
    public EmbeddingSearchResult<TextSegment> search(EmbeddingSearchRequest request) {
        // 实现基于向量的搜索
        String sql = String.format(
            "SELECT embedding_id, (2 - (embedding <=> ?)) / 2 AS score, embedding, text, %s " +
            "FROM %s " +
            "WHERE (2 - (embedding <=> ?)) / 2 >= ? " +
            "ORDER BY score DESC " +
            "LIMIT ?",
            String.join(",", metadataHandler.columnsNames()),
            tableName);

        List<EmbeddingMatch<TextSegment>> matches = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            PGvector vector = new PGvector(request.queryEmbedding().vector());
            
            stmt.setObject(1, vector);
            stmt.setObject(2, vector);
            stmt.setDouble(3, request.minScore());
            stmt.setInt(4, request.maxResults());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("embedding_id");
                    double score = rs.getDouble("score");
                    float[] vectorArray = ((PGvector)rs.getObject("embedding")).toArray();
                    String text = rs.getString("text");
                    
                    Metadata metadata = metadataHandler.fromResultSet(rs);
                    TextSegment segment = text != null ? 
                        TextSegment.from(text, metadata) : null;
                    
                    matches.add(new EmbeddingMatch<>(
                        score, id, new Embedding(vectorArray), segment));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Search failed", e);
        }
        
        return new EmbeddingSearchResult<>(matches);
    }
}