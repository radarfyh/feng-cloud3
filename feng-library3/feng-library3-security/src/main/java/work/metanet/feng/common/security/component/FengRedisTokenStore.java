// package work.metanet.feng.common.security.component;

// import cn.hutool.core.util.StrUtil;
// import lombok.extern.slf4j.Slf4j;
// import work.metanet.feng.common.core.constant.enums.JobCategory;
// import work.metanet.feng.common.core.util.KeyStrResolver;
// import org.springframework.data.redis.connection.RedisConnection;
// import org.springframework.data.redis.connection.RedisConnectionFactory;
// import org.springframework.data.redis.connection.RedisZSetCommands;
// import org.springframework.data.redis.core.Cursor;
// import org.springframework.data.redis.core.ScanOptions;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
// import org.springframework.security.oauth2.common.OAuth2AccessToken;
// import org.springframework.security.oauth2.common.OAuth2RefreshToken;
// import org.springframework.security.oauth2.provider.OAuth2Authentication;
// import org.springframework.security.oauth2.provider.OAuth2Request;
// import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
// import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
// import org.springframework.security.oauth2.provider.token.TokenStore;
// import org.springframework.security.oauth2.provider.token.store.redis.JdkSerializationStrategy;
// import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
// import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStoreSerializationStrategy;
// import org.springframework.util.ClassUtils;
// import org.springframework.util.ReflectionUtils;

// import com.fasterxml.jackson.annotation.JsonCreator;
// import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
// import com.fasterxml.jackson.annotation.JsonProperty;
// import com.fasterxml.jackson.annotation.JsonSubTypes;
// import com.fasterxml.jackson.annotation.JsonTypeInfo;
// import com.fasterxml.jackson.annotation.JsonValue;
// import com.fasterxml.jackson.core.JsonGenerator;
// import com.fasterxml.jackson.core.JsonParseException;
// import com.fasterxml.jackson.core.JsonParser;
// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.databind.DeserializationContext;
// import com.fasterxml.jackson.databind.DeserializationFeature;
// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.databind.SerializerProvider;
// import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
// import com.fasterxml.jackson.databind.module.SimpleModule;
// import com.fasterxml.jackson.databind.ser.std.StdSerializer;

// import java.io.IOException;
// import java.io.Serializable;
// import java.lang.reflect.Method;
// import java.util.*;
// /**
//  * FengRedisTokenStore 继承了 RedisTokenStore，并提供优化的 token 管理功能，
//  * 包括对客户端特定的 token 过期时间管理和定期清理（垃圾回收）功能。
//  * 本类重写了获取、存储、删除 OAuth2 访问令牌及刷新令牌的逻辑，并且支持 Redis 上的多种 token 管理方式。
//  * 
//  * 主要改进：
//  * 1. 增强了 Redis 连接的使用效率，避免了重复的连接打开/关闭操作。
//  * 2. 增加了 Redis 中 token 的定期过期清理功能。
//  * 3. 优化了异常处理，提供更为详细的错误日志。
//  */
// @Slf4j
// public class FengRedisTokenStore implements TokenStore {
//     // 定义 Redis 中存储的 token 键的前缀
//     private static final String ACCESS = "access:";
//     private static final String AUTH_TO_ACCESS = "auth_to_access:";
//     private static final String AUTH = "auth:";
//     private static final String REFRESH_AUTH = "refresh_auth:";
//     private static final String REFRESH = "refresh:";
//     private static final String REFRESH_TO_ACCESS = "refresh_to_access:";
//     private static final String CLIENT_ID_TO_ACCESS = "client_id_to_access_z:";
//     private static final String UNAME_TO_ACCESS = "uname_to_access_z:";

//     private static final boolean springDataRedis_2_0 = ClassUtils.isPresent(
//             "org.springframework.data.redis.connection.RedisStandaloneConfiguration",
//             RedisTokenStore.class.getClassLoader());

//     private final RedisConnectionFactory connectionFactory;  // Redis 连接工厂
//     private final KeyStrResolver keyStrResolver;  // 用于生成 Redis 键的解析器

//     private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();  // 默认的认证键生成器
//     private String prefix = "";  // Redis 键前缀

//     private Method redisConnectionSet_2_0;  // 用于 Redis 2.0 的特殊设置方法

// 	private RedisTokenStoreSerializationStrategy serializationStrategy = new JdkSerializationStrategy();  // 默认的序列化策略
// //    private RedisTokenStoreSerializationStrategy serializationStrategy = new FengJsonSerializationStrategy();  // 使用自定义JSON序列化

//     // 自定义JSON序列化策略
//     public class FengJsonSerializationStrategy extends JdkSerializationStrategy {
//         private final ObjectMapper objectMapper;

//         public FengJsonSerializationStrategy() {
//             this.objectMapper = createConfiguredObjectMapper();
//         }

//         private ObjectMapper createConfiguredObjectMapper() {
//             ObjectMapper mapper = new ObjectMapper();
            
//             // 禁用未知属性检查
//             mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            
//             // 启用默认类型信息
//             mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
            
//             SimpleModule module = new SimpleModule("SecuritySerializationModule")
//             		//注释掉的原因是，在枚举类JobCategory中加了序列化和反序列化的注解
//             		//.addSerializer(JobCategory.class, new JobCategorySerializer())
//             		//.addDeserializer(JobCategory.class, new JobCategoryDeserializer())
//             		.addDeserializer(OAuth2Authentication.class, new OAuth2AuthenticationDeserializer());
            
//             mapper.registerModule(module);
            
//             // 注册Mixin
//             mapper.addMixIn(OAuth2Request.class, OAuth2RequestMixin.class);
//             mapper.addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityMixin.class);
            
//             return mapper;
//         }
//     }
    
//     /**
//      * OAuth2反序列化器
//      */
//     class OAuth2AuthenticationDeserializer extends StdDeserializer<OAuth2Authentication> {
//         public OAuth2AuthenticationDeserializer() {
//             super(OAuth2Authentication.class);
//         }

//         @Override
//         public OAuth2Authentication deserialize(JsonParser p, DeserializationContext ctxt) 
//             throws IOException {
//             JsonNode node = p.getCodec().readTree(p);
//             ObjectMapper mapper = (ObjectMapper) p.getCodec();
            
//             // 反序列化OAuth2Request
//             JsonNode requestNode = node.get("oauth2Request");
//             OAuth2Request request = mapper.readValue(mapper.treeAsTokens(requestNode), OAuth2Request.class);
            
//             // 反序列化用户认证(可能为null)
//             Authentication userAuth = null;
//             if (node.has("userAuthentication")) {
//                 JsonNode authNode = node.get("userAuthentication");
//                 userAuth = mapper.readValue(mapper.treeAsTokens(authNode), Authentication.class);
//             }
            
//             return new OAuth2Authentication(request, userAuth);
//         }
//     }   
    
//     /**
//      * JobCategory序列化器
//      */
//     public class JobCategorySerializer extends StdSerializer<JobCategory> {
// 		public JobCategorySerializer() {
//             super(JobCategory.class);
//         }

//         @Override
//         public void serialize(JobCategory value, JsonGenerator gen, SerializerProvider provider) 
//             throws IOException {
//             // 将枚举序列化为它的name()字符串
//             gen.writeString(value.name());
//         }
//     }

//     /**
//      * JobCategory反序列化器
//      */
//     public class JobCategoryDeserializer extends StdDeserializer<JobCategory> {
        
//         public JobCategoryDeserializer() {
//             super(JobCategory.class);
//         }

//         @Override
//         public JobCategory deserialize(JsonParser p, DeserializationContext ctxt) 
//             throws IOException, JsonProcessingException {
//             // 从字符串反序列化为枚举
//             String value = p.getText();
//             try {
//                 return JobCategory.valueOf(value);
//             } catch (IllegalArgumentException e) {
//                 // 处理未知枚举值的情况
//                 throw new JsonParseException(p, "Unknown JobCategory value: " + value, e);
//             }
//         }
//     }
//     /**
//      * OAuth2Request的Mixin类，用于配置JSON序列化/反序列化规则
//      */
//     @JsonIgnoreProperties(ignoreUnknown = true)
//     @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
//     @JsonSubTypes({@JsonSubTypes.Type(value = OAuth2Request.class, name = "oauth2Request")})
//     abstract class OAuth2RequestMixin {
//         @JsonCreator
//         public OAuth2RequestMixin(
//             @JsonProperty("requestParameters") Map<String, String> requestParameters,
//             @JsonProperty("clientId") String clientId,
//             @JsonProperty("authorities") Collection<? extends GrantedAuthority> authorities,
//             @JsonProperty("approved") boolean approved,
//             @JsonProperty("scope") Set<String> scope,
//             @JsonProperty("resourceIds") Set<String> resourceIds,
//             @JsonProperty("redirectUri") String redirectUri,
//             @JsonProperty("responseTypes") Set<String> responseTypes,
//             @JsonProperty("extensions") Map<String, Serializable> extensions) {
//         }
//     }

//     /**
//      * SimpleGrantedAuthority的Mixin类
//      */
//     abstract class SimpleGrantedAuthorityMixin {
        
//         @JsonCreator
//         public SimpleGrantedAuthorityMixin(@JsonProperty("authority") String role) {
//         }
        
//         @JsonValue
//         public abstract String getAuthority();
//     }
    
//     /**
//      * FengRedisTokenStore 构造函数
//      * <p>
//      * 初始化 Redis 连接工厂和键解析器，并根据 Redis 版本加载连接方法。
//      * </p>
//      * @param connectionFactory Redis 连接工厂
//      * @param resolver 用于生成 Redis 键的解析器
//      */
//     public FengRedisTokenStore(RedisConnectionFactory connectionFactory, KeyStrResolver resolver) {
//         this.keyStrResolver = resolver;
//         this.connectionFactory = connectionFactory;
//         if (springDataRedis_2_0) {
//             this.loadRedisConnectionMethods_2_0();  // 加载 Redis 2.0 连接方法
//         }
//         log.debug("FengRedisTokenStore initialized with resolver: {}", resolver);
//     }

//     /**
//      * 设置认证键生成器
//      * <p>
//      * 用于生成认证相关的 Redis 键。
//      * </p>
//      * @param authenticationKeyGenerator 认证键生成器
//      */
//     public void setAuthenticationKeyGenerator(AuthenticationKeyGenerator authenticationKeyGenerator) {
//         this.authenticationKeyGenerator = authenticationKeyGenerator;
//     }

//     /**
//      * 设置 Redis 序列化策略
//      * <p>
//      * 用于定制如何序列化存储在 Redis 中的数据。
//      * </p>
//      * @param serializationStrategy 序列化策略
//      */
//     public void setSerializationStrategy(RedisTokenStoreSerializationStrategy serializationStrategy) {
//         if (serializationStrategy == null) {
//             throw new IllegalArgumentException("Serialization strategy cannot be null");
//         }
//         this.serializationStrategy = serializationStrategy;
//     }

//     /**
//      * 设置 Redis 键的前缀
//      * <p>
//      * 在 Redis 键前加上自定义前缀，避免键冲突。
//      * </p>
//      * @param prefix Redis 键前缀
//      */
//     public void setPrefix(String prefix) {
//         if (prefix == null || prefix.isEmpty()) {
//             throw new IllegalArgumentException("Prefix cannot be null or empty");
//         }
//         this.prefix = prefix;
//     }

//     /**
//      * 加载 Redis 2.0 特定的连接方法
//      * <p>
//      * 该方法通过反射机制加载 RedisConnection 类中用于设置键值对的方法，支持 Redis 2.0 的特定实现。
//      * 主要用于 Redis 2.0 中的操作，以提高 Redis 连接的操作效率和兼容性。
//      * </p>
//      */
//     private void loadRedisConnectionMethods_2_0() {
//         // 使用反射查找 RedisConnection 中 set 方法
//         this.redisConnectionSet_2_0 = ReflectionUtils.findMethod(RedisConnection.class, "set", byte[].class, byte[].class);
        
//         // 检查方法是否存在
//         if (this.redisConnectionSet_2_0 == null) {
//             log.warn("未找到 RedisConnection 类中的 set(byte[], byte[]) 方法，Redis 2.0 连接方法加载失败。");
//         } else {
//             log.debug("Redis 2.0 特定连接方法已成功加载：{}", redisConnectionSet_2_0.getName());
//         }
//     }


//     /**
//      * 获取 Redis 连接
//      * <p>
//      * 该方法通过 RedisConnectionFactory 获取 Redis 连接，确保在 Redis 操作时能够正常访问 Redis 服务。
//      * </p>
//      * 
//      * @return 返回 RedisConnection 对象
//      * @throws IllegalStateException 如果 RedisConnectionFactory 为 null，则抛出该异常
//      */
//     private RedisConnection getConnection() {
//         // 判断 connectionFactory 是否为 null，防止空指针异常
//         if (connectionFactory == null) {
//             throw new IllegalStateException("RedisConnectionFactory cannot be null");
//         }

//         RedisConnection connection = connectionFactory.getConnection();

//         // 日志记录：连接 Redis 成功
//         log.debug("Successfully obtained Redis connection: {}", connection);

//         return connection;
//     }


//     /**
//      * 序列化对象
//      * <p>
//      * 该方法使用指定的序列化策略将传入的对象序列化为字节数组。
//      * 如果对象为 null，抛出 IllegalArgumentException 异常。
//      * </p>
//      * 
//      * @param object 需要序列化的对象
//      * @return 序列化后的字节数组
//      * @throws IllegalArgumentException 如果对象为 null，则抛出该异常
//      */
//     private byte[] serialize(Object object) {
//         // 检查传入的对象是否为 null，若是则抛出异常
//         if (object == null) {
//             throw new IllegalArgumentException("Cannot serialize a null object");
//         }

//         // 记录序列化操作的日志
//         log.debug("Serializing object of type: {}", object.getClass().getName());

//         // 使用序列化策略进行序列化操作
//         byte[] serializedObject = serializationStrategy.serialize(object);

//         // 日志记录：成功序列化对象
//         log.debug("Successfully serialized object: {}", object);

//         return serializedObject;
//     }

//     /**
//      * 序列化键
//      * <p>
//      * 该方法首先通过 `keyStrResolver` 对象提取并格式化传入的键，然后将其序列化为字节数组。
//      * 如果传入的键为 null 或空字符串，则抛出非法参数异常。
//      * </p>
//      *
//      * @param object 需要序列化的键，通常是一个字符串形式的对象标识
//      * @return 序列化后的字节数组
//      * @throws IllegalArgumentException 如果传入的键为空或无效，则抛出该异常
//      */
//     private byte[] serializeKey(String object) {
//         // 检查输入的键是否为空或 null
//         if (StrUtil.isBlank(object)) {
//             throw new IllegalArgumentException("Cannot serialize an empty or null key.");
//         }

//         // 使用 keyStrResolver 提取键并记录日志
//         String extractedKey = keyStrResolver.extract((prefix + object), StrUtil.COLON);
//         log.debug("Extracted key: {}", extractedKey);

//         // 进行序列化操作
//         byte[] serializedKey = serialize(extractedKey);

//         // 返回序列化后的字节数组
//         return serializedKey;
//     }


//     // 反序列化 OAuth2 访问令牌
//     private OAuth2AccessToken deserializeAccessToken(byte[] bytes) {
//         return serializationStrategy.deserialize(bytes, OAuth2AccessToken.class);
//     }

//     // 反序列化 OAuth2 认证信息
//     private OAuth2Authentication deserializeAuthentication(byte[] bytes) {
//         return serializationStrategy.deserialize(bytes, OAuth2Authentication.class);
//     }
    
// 	private OAuth2RefreshToken deserializeRefreshToken(byte[] bytes) {
// 		return serializationStrategy.deserialize(bytes, OAuth2RefreshToken.class);
// 	}

// 	private byte[] serialize(String string) {
// 		return serializationStrategy.serialize(string);
// 	}

// 	private String deserializeString(byte[] bytes) {
// 		return serializationStrategy.deserializeString(bytes);
// 	}

// 	/**
// 	 * 获取访问令牌
// 	 * <p>
// 	 * 该方法根据给定的 OAuth2Authentication 获取与之关联的访问令牌。如果令牌不存在，或者令牌与认证信息不匹配， 
// 	 * 则重新存储该令牌并保持认证信息的一致性。
// 	 * </p>
// 	 *
// 	 * @param authentication OAuth2Authentication 对象，包含认证信息
// 	 * @return 返回获取的 OAuth2AccessToken 对象，如果没有找到相应的访问令牌，则返回 null
// 	 */
// 	@Override
// 	public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
// 	    // 从认证信息中提取认证键
// 	    String key = authenticationKeyGenerator.extractKey(authentication);
	    
// 	    // 序列化并生成 Redis 键
// 	    byte[] serializedKey = serializeKey(AUTH_TO_ACCESS + key);
// 	    byte[] bytes;
	    
// 	    // 获取 Redis 连接并尝试从 Redis 获取存储的访问令牌
// 	    try (RedisConnection conn = getConnection()) {
// 	        bytes = conn.get(serializedKey);
// 	    }
	    
// 	    if (bytes == null || bytes.length == 0) {
// 	        // 如果未找到相应的访问令牌，记录日志并返回 null
// 	        log.debug("Access token not found for key: {}", key);
// 	        return null;
// 	    }

// 	    // 反序列化访问令牌
// 	    OAuth2AccessToken accessToken = deserializeAccessToken(bytes);
	    
// 	    if (accessToken != null) {
// 	        // 获取存储的认证信息并进行匹配检查
// 	        OAuth2Authentication storedAuthentication = readAuthentication(accessToken.getValue());
	        
// 	        if (storedAuthentication == null || !key.equals(authenticationKeyGenerator.extractKey(storedAuthentication))) {
// 	            // 如果认证信息不一致或未找到存储的认证信息，重新存储访问令牌
// 	            log.debug("Access token authentication mismatch, re-storing access token.");
// 	            storeAccessToken(accessToken, authentication);
// 	        }
// 	    } else {
// 	        // 如果反序列化失败，记录日志
// 	        log.error("Failed to deserialize access token for key: {}", key);
// 	    }
	    
// 	    // 返回访问令牌
// 	    return accessToken;
// 	}

	
// 	@Override
// 	public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
// 		return readAuthentication(token.getValue());
// 	}
	
//     /**
//      * 从 Redis 中读取 OAuth2Authentication 信息
//      * <p>
//      * 该方法用于根据给定的访问令牌（token）从 Redis 中读取对应的 OAuth2Authentication 信息。
//      * 在调用时，Redis 存储的 OAuth2Authentication 信息会通过序列化和反序列化机制还原成对应的对象。
//      * </p>
//      * 
//      * @param token 访问令牌（token），该令牌用于在 Redis 中查找相关的 OAuth2Authentication 信息。
//      * @return 返回一个 OAuth2Authentication 对象，表示与该访问令牌相关联的认证信息。如果未找到对应的认证信息，则返回 null。
//      * @throws InvalidTokenException 如果访问令牌无效或无法找到对应的认证信息，则抛出该异常。
//      */
//     @Override
//     public OAuth2Authentication readAuthentication(String token) {
//         byte[] bytes;
//         try (RedisConnection conn = getConnection()) {
//             // 获取 Redis 连接后，从存储的键值中取出访问令牌对应的字节数组
//             bytes = conn.get(serializeKey(AUTH + token));
//         }
//         // 反序列化字节数组，将其转回为 OAuth2Authentication 对象
//         return deserializeAuthentication(bytes);
//     }
    
// 	@Override
// 	public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
// 		return readAuthenticationForRefreshToken(token.getValue());
// 	}

// 	/**
// 	 * 从 Redis 中读取与刷新令牌关联的认证信息
// 	 * <p>
// 	 * 该方法根据给定的刷新令牌从 Redis 中查找相应的 OAuth2Authentication 信息，并返回该认证信息。
// 	 * 如果未能成功读取或反序列化数据，则返回 null。
// 	 * </p>
// 	 *
// 	 * @param token 刷新令牌，用于在 Redis 中查找与之关联的认证信息。
// 	 * @return 返回与刷新令牌关联的 OAuth2Authentication 信息，如果未找到或无法读取，返回 null。
// 	 */
// 	public OAuth2Authentication readAuthenticationForRefreshToken(String token) {
// 	    // 构建 Redis 键
// 	    byte[] key = serializeKey(REFRESH_AUTH + token);
// 	    byte[] bytes;
	    
// 	    // 获取 Redis 连接并从 Redis 中获取存储的认证信息
// 	    try (RedisConnection conn = getConnection()) {
// 	        bytes = conn.get(key);
	        
// 	        // 如果未能从 Redis 获取数据，记录日志并返回 null
// 	        if (bytes == null || bytes.length == 0) {
// 	            log.debug("No authentication found for refresh token: {}", token);
// 	            return null;
// 	        }

// 	        // 反序列化数据为 OAuth2Authentication 对象
// 	        return deserializeAuthentication(bytes);
	        
// 	    } catch (Exception e) {
// 	        // 如果发生任何异常（如 Redis 连接问题、反序列化失败等），记录错误日志
// 	        log.error("Failed to read authentication for refresh token: {}", token, e);
// 	        return null;
// 	    }
// 	}
	
//     /**
//      * 存储 OAuth2 访问令牌到 Redis
//      * <p>
//      * 该方法用于将 OAuth2 访问令牌（access token）及其对应的认证信息（OAuth2Authentication）存储到 Redis。
//      * 访问令牌和认证信息会被序列化并存储在多个 Redis 键中，以确保能够在不同的场景下有效地检索和使用。
//      * </p>
//      * 
//      * @param token OAuth2 访问令牌（access token），它包含了对资源服务器的授权。
//      * @param authentication OAuth2Authentication 对象，包含了与访问令牌相关联的用户认证信息。
//      */
//     @Override
//     public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
//         // 序列化访问令牌和认证信息
//         byte[] serializedAccessToken = serialize(token);
//         byte[] serializedAuth = serialize(authentication);
//         // 创建对应的 Redis 键
//         byte[] accessKey = serializeKey(ACCESS + token.getValue());
//         byte[] authKey = serializeKey(AUTH + token.getValue());
//         byte[] authToAccessKey = serializeKey(AUTH_TO_ACCESS + authenticationKeyGenerator.extractKey(authentication));
//         byte[] approvalKey = serializeKey(UNAME_TO_ACCESS + getApprovalKey(authentication));
//         byte[] clientId = serializeKey(CLIENT_ID_TO_ACCESS + authentication.getOAuth2Request().getClientId());

//         try (RedisConnection conn = getConnection()) {
//             // 开启 Redis 管道操作
//             conn.openPipeline();
            
//             // 根据 Redis 版本调用不同的方法来存储数据
//             if (springDataRedis_2_0) {
//                 try {
//                     this.redisConnectionSet_2_0.invoke(conn, accessKey, serializedAccessToken);
//                     this.redisConnectionSet_2_0.invoke(conn, authKey, serializedAuth);
//                     this.redisConnectionSet_2_0.invoke(conn, authToAccessKey, serializedAccessToken);
//                 } catch (Exception ex) {
//                     throw new RuntimeException(ex);
//                 }
//             } else {
//                 // 存储访问令牌和认证信息
//                 conn.set(accessKey, serializedAccessToken);
//                 conn.set(authKey, serializedAuth);
//                 conn.set(authToAccessKey, serializedAccessToken);
//             }

//             // 如果访问令牌有过期时间，设置过期时间
//             if (token.getExpiration() != null) {
//                 int seconds = token.getExpiresIn();
//                 long expirationTime = token.getExpiration().getTime();

//                 // 如果不是客户端仅限授权，添加到审批列表和客户端访问列表
//                 if (!authentication.isClientOnly()) {
//                     conn.zAdd(approvalKey, expirationTime, serializedAccessToken);
//                 }
//                 conn.zAdd(clientId, expirationTime, serializedAccessToken);

//                 // 设置过期时间
//                 conn.expire(accessKey, seconds);
//                 conn.expire(authKey, seconds);
//                 conn.expire(authToAccessKey, seconds);
//                 conn.expire(clientId, seconds);
//                 conn.expire(approvalKey, seconds);
//             } else {
//                 // 如果没有过期时间，将令牌添加到客户端访问列表中，且设置为永久
//                 conn.zAdd(clientId, -1, serializedAccessToken);
//                 if (!authentication.isClientOnly()) {
//                     conn.zAdd(approvalKey, -1, serializedAccessToken);
//                 }
//             }

//             // 如果访问令牌有刷新令牌，存储刷新令牌到 Redis
//             OAuth2RefreshToken refreshToken = token.getRefreshToken();
//             if (refreshToken != null && refreshToken.getValue() != null) {
//                 byte[] auth = serialize(token.getValue());
//                 byte[] refreshToAccessKey = serializeKey(REFRESH_TO_ACCESS + token.getRefreshToken().getValue());
//                 if (springDataRedis_2_0) {
//                     try {
//                         this.redisConnectionSet_2_0.invoke(conn, refreshToAccessKey, auth);
//                     } catch (Exception ex) {
//                         throw new RuntimeException(ex);
//                     }
//                 } else {
//                     conn.set(refreshToAccessKey, auth);
//                 }
//                 if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
//                     ExpiringOAuth2RefreshToken expiringRefreshToken = (ExpiringOAuth2RefreshToken) refreshToken;
//                     Date expiration = expiringRefreshToken.getExpiration();
//                     if (expiration != null) {
//                         int seconds = Long.valueOf((expiration.getTime() - System.currentTimeMillis()) / 1000L).intValue();
//                         conn.expire(refreshToAccessKey, seconds);
//                     }
//                 }
//             }
//             // 提交 Redis 管道操作
//             conn.closePipeline();
//         }
//     }
    
//     /**
//      * 获取批准键，基于 OAuth2Authentication 对象中的客户端 ID 和用户名。
//      * <p>
//      * 该方法将根据 OAuth2Authentication 对象中的客户端 ID 和用户名生成批准键，
//      * 如果用户名为空，则返回客户端 ID，若用户名存在，则客户端 ID 和用户名用冒号连接。
//      * </p>
//      *
//      * @param authentication OAuth2Authentication 对象，包含客户端 ID 和用户信息
//      * @return 返回生成的批准键，格式为 "clientId:username"，如果用户名为空，则只返回 clientId
//      */
//     private static String getApprovalKey(OAuth2Authentication authentication) {
//         // 获取用户名，如果用户认证信息为空，则默认为空字符串
//         String userName = Optional.ofNullable(authentication.getUserAuthentication())
//                                   .map(auth -> auth.getName())
//                                   .orElse("");
//         // 通过客户端 ID 和用户名生成批准键
//         return getApprovalKey(authentication.getOAuth2Request().getClientId(), userName);
//     }

//     /**
//      * 获取批准键，基于客户端 ID 和用户名。
//      * <p>
//      * 该方法生成的批准键格式为 "clientId:username"，如果用户名为空，则只返回 clientId。
//      * </p>
//      *
//      * @param clientId 客户端 ID
//      * @param userName 用户名
//      * @return 返回生成的批准键，格式为 "clientId:username"，如果用户名为空，则只返回 clientId
//      */
//     private static String getApprovalKey(String clientId, String userName) {
//         // 直接通过三元运算符处理用户名为空的情况
//         return clientId + (userName != null && !userName.isEmpty() ? ":" + userName : "");
//     }
    
//     /**
//      * 根据 OAuth2 访问令牌移除相关的存储数据
//      * <p>
//      * 该方法接收一个 OAuth2AccessToken 对象，并从 Redis 中删除与该访问令牌相关联的所有数据。
//      * </p>
//      *
//      * @param accessToken OAuth2 访问令牌
//      */
//     @Override
//     public void removeAccessToken(OAuth2AccessToken accessToken) {
//         // 调用根据令牌值移除访问令牌的方法
//         removeAccessToken(accessToken.getValue());
//     }
    
//     /**
//      * 从 Redis 中读取指定的 OAuth2 访问令牌
//      * <p>
//      * 该方法根据给定的访问令牌值，从 Redis 中读取对应的 OAuth2 访问令牌数据并反序列化为 OAuth2AccessToken 对象。
//      * 如果读取失败，返回 null。
//      * </p>
//      *
//      * @param tokenValue 访问令牌值
//      * @return 返回 OAuth2AccessToken 对象，如果未找到，则返回 null。
//      */
//     @Override
//     public OAuth2AccessToken readAccessToken(String tokenValue) {
//         byte[] key = serializeKey(ACCESS + tokenValue);
//         byte[] bytes;
//         try (RedisConnection conn = getConnection()) {
//             // 获取 Redis 连接并读取指定令牌值的字节数据
//             bytes = conn.get(key);
//         } catch (Exception e) {
//             log.error("Failed to read access token for token value: {}", tokenValue, e);
//             return null;  // 失败时返回 null，可以根据需求做更复杂的异常处理
//         }
//         // 反序列化字节数组，返回 OAuth2AccessToken 对象
//         return deserializeAccessToken(bytes);
//     }

//     /**
//      * 根据访问令牌值移除访问令牌及其相关认证信息
//      * <p>
//      * 该方法首先从 Redis 中获取与访问令牌相关的所有信息，包括访问令牌和认证信息。
//      * 然后，删除这些信息并清理 Redis 中的相应条目。
//      * </p>
//      *
//      * @param tokenValue 访问令牌值
//      */
//     private void removeAccessToken(String tokenValue) {
//         byte[] accessKey = serializeKey(ACCESS + tokenValue);
//         byte[] authKey = serializeKey(AUTH + tokenValue);
//         try (RedisConnection conn = getConnection()) {
//             // 打开 Redis 管道操作以提高效率
//             conn.openPipeline();
//             // 获取访问令牌和认证信息
//             conn.get(accessKey);
//             conn.get(authKey);
//             // 删除访问令牌和认证信息
//             conn.del(accessKey);
//             conn.del(authKey);
            
//             // 获取操作结果
//             List<Object> results = conn.closePipeline();
//             byte[] access = (byte[]) results.get(0);
//             byte[] auth = (byte[]) results.get(1);

//             OAuth2Authentication authentication = deserializeAuthentication(auth);
//             if (authentication != null) {
//                 String key = authenticationKeyGenerator.extractKey(authentication);
//                 byte[] authToAccessKey = serializeKey(AUTH_TO_ACCESS + key);
                
//                 // 清除认证和访问令牌的映射
//                 conn.openPipeline();
//                 conn.del(authToAccessKey);
//                 conn.closePipeline();
//             }
//         } catch (Exception e) {
//             log.error("Error removing access token: {}", tokenValue, e);
//             // 可以根据需求抛出运行时异常或执行其他处理
//         }
//     }

//     /**
//      * 存储刷新令牌及其认证信息
//      * <p>
//      * 该方法将刷新令牌和对应的认证信息存储到 Redis 中。
//      * 如果刷新令牌是可过期的，还会设置过期时间。使用 Redis 管道操作提高性能。
//      * </p>
//      *
//      * @param refreshToken OAuth2 刷新令牌
//      * @param authentication OAuth2 认证信息
//      */
//     @Override
//     public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
//         byte[] refreshKey = serializeKey(REFRESH + refreshToken.getValue());  // 刷新令牌的 Redis 键
//         byte[] refreshAuthKey = serializeKey(REFRESH_AUTH + refreshToken.getValue());  // 刷新令牌的认证信息 Redis 键
//         byte[] serializedRefreshToken = serialize(refreshToken);  // 序列化刷新令牌

//         try (RedisConnection conn = getConnection()) {
//             // 开启 Redis 管道操作以提高效率
//             conn.openPipeline();

//             // 根据 Redis 版本执行存储操作
//             if (springDataRedis_2_0) {
//                 try {
//                     this.redisConnectionSet_2_0.invoke(conn, refreshKey, serializedRefreshToken);
//                     this.redisConnectionSet_2_0.invoke(conn, refreshAuthKey, serialize(authentication));
//                 } catch (Exception ex) {
//                     log.error("Failed to store refresh token and auth in Redis", ex);
//                     throw new RuntimeException("Failed to store refresh token and auth in Redis", ex);  // 抛出运行时异常
//                 }
//             } else {
//                 // Redis 2.0 以下版本直接使用 set 操作
//                 conn.set(refreshKey, serializedRefreshToken);
//                 conn.set(refreshAuthKey, serialize(authentication));
//             }

//             // 设置过期时间（如果有）
//             if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
//                 ExpiringOAuth2RefreshToken expiringRefreshToken = (ExpiringOAuth2RefreshToken) refreshToken;
//                 Date expiration = expiringRefreshToken.getExpiration();
//                 if (expiration != null) {
//                     int seconds = (int) ((expiration.getTime() - System.currentTimeMillis()) / 1000L);  // 计算过期秒数
//                     conn.expire(refreshKey, seconds);  // 设置刷新令牌的过期时间
//                     conn.expire(refreshAuthKey, seconds);  // 设置认证信息的过期时间
//                 }
//             }

//             // 提交管道操作
//             conn.closePipeline();
//         } catch (Exception e) {
//             log.error("Error while storing refresh token: {}", refreshToken.getValue(), e);
//             throw new RuntimeException("Error while storing refresh token", e);  // 处理异常并抛出
//         }
//     }

//     /**
//      * 从 Redis 中读取刷新令牌
//      * <p>
//      * 该方法用于根据给定的刷新令牌值从 Redis 中读取对应的 OAuth2 刷新令牌。
//      * 如果 Redis 中存在对应的令牌，返回反序列化后的 OAuth2RefreshToken 对象，否则返回 null。
//      * </p>
//      * 
//      * @param tokenValue 刷新令牌的值
//      * @return 返回 OAuth2RefreshToken 对象，如果未找到则返回 null
//      */
//     @Override
//     public OAuth2RefreshToken readRefreshToken(String tokenValue) {
//         byte[] key = serializeKey(REFRESH + tokenValue);  // 构造 Redis 键
//         byte[] bytes = null;
//         try (RedisConnection conn = getConnection()) {
//             // 从 Redis 获取存储的刷新令牌
//             bytes = conn.get(key);
//         } catch (Exception e) {
//             // 捕获 Redis 操作的异常并记录错误日志
//             log.error("Error occurred while reading refresh token from Redis for token: {}", tokenValue, e);
//             throw new RuntimeException("Failed to read refresh token from Redis", e);  // 抛出异常进行传递
//         }
        
//         // 如果没有找到对应的刷新令牌，则返回 null
//         if (bytes == null || bytes.length == 0) {
//             log.debug("No refresh token found for token: {}", tokenValue);
//             return null;
//         }

//         // 反序列化字节数组为 OAuth2RefreshToken 对象并返回
//         return deserializeRefreshToken(bytes);
//     }

	
//     // 移除刷新令牌
//     @Override
//     public void removeRefreshToken(OAuth2RefreshToken refreshToken) {
//         removeRefreshToken(refreshToken.getValue());
//     }

//     // 根据刷新令牌值移除刷新令牌
//     public void removeRefreshToken(String tokenValue) {
//         byte[] refreshKey = serializeKey(REFRESH + tokenValue);
//         byte[] refreshAuthKey = serializeKey(REFRESH_AUTH + tokenValue);
//         try (RedisConnection conn = getConnection()) {
//             conn.del(refreshKey);  // 删除刷新令牌
//             conn.del(refreshAuthKey);  // 删除与刷新令牌相关的认证信息
//         }
//     }
    
// 	@Override
// 	public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
// 		removeAccessTokenUsingRefreshToken(refreshToken.getValue());
// 	}

// 	/**
// 	 * 根据刷新令牌移除关联的访问令牌
// 	 * <p>
// 	 * 该方法根据给定的刷新令牌（refreshToken），从 Redis 中查找关联的访问令牌，并移除该访问令牌。
// 	 * 如果找到关联的访问令牌，首先删除刷新令牌和关联信息，然后删除访问令牌。
// 	 * </p>
// 	 * 
// 	 * @param refreshToken 刷新令牌的值
// 	 */
// 	private void removeAccessTokenUsingRefreshToken(String refreshToken) {
// 	    byte[] key = serializeKey(REFRESH_TO_ACCESS + refreshToken);  // 构造 Redis 键
// 	    List<Object> results = null;
	    
// 	    try (RedisConnection conn = getConnection()) {
// 	        // 打开 Redis 管道以优化批量操作
// 	        conn.openPipeline();
// 	        conn.get(key);  // 获取与刷新令牌关联的访问令牌
// 	        conn.del(key);  // 删除刷新令牌和关联信息
// 	        results = conn.closePipeline();  // 提交并获取操作结果
// 	    } catch (Exception e) {
// 	        // 捕获 Redis 操作异常并记录错误日志
// 	        log.error("Error occurred while removing access token using refresh token: {}", refreshToken, e);
// 	        throw new RuntimeException("Failed to remove access token using refresh token", e);  // 抛出异常进行传递
// 	    }
	    
// 	    // 从 Redis 操作结果中获取访问令牌
// 	    byte[] bytes = (byte[]) results.get(0);
// 	    String accessToken = deserializeString(bytes);
	    
// 	    if (accessToken != null) {
// 	        // 如果找到了访问令牌，则移除访问令牌
// 	        removeAccessToken(accessToken);
// 	    } else {
// 	        log.debug("No access token found for refresh token: {}", refreshToken);
// 	    }
// 	}
	
//     /**
//      * 获取 Redis 中 ZSet 结构的字节列表
//      * <p>
//      * Redis 中使用 ZSet 存储访问令牌时，存储结构为有序集合，本方法用于获取符合条件的访问令牌。
//      * </p>
//      * @param key Redis 键
//      * @param conn Redis 连接
//      * @return 存储的字节列表
//      */
//     private List<byte[]> getZByteLists(byte[] key, RedisConnection conn) {
//         // 对 ZSet 进行过期维护
//         long currentTime = System.currentTimeMillis();
//         conn.zRemRangeByScore(key, 0, currentTime);

//         List<byte[]> byteList;
//         Long size = conn.zCard(key);
//         assert size != null;
//         byteList = new ArrayList<>(size.intValue());
//         Cursor<RedisZSetCommands.Tuple> cursor = conn.zScan(key, ScanOptions.NONE);

//         while (cursor.hasNext()) {
//             RedisZSetCommands.Tuple t = cursor.next();

//             // 如果 score 为 -1 或者 score 大于当前时间，则添加该元素
//             if (t.getScore() == -1 || t.getScore() > currentTime) {
//                 byteList.add(t.getValue());
//             }
//         }
//         return byteList;
//     }
    
//     /**
//      * 执行 Redis 的垃圾回收，清理过期的访问令牌
//      * <p>
//      * 该方法会扫描 Redis 中存储的 `client_id_to_access` 和 `uname_to_access` 列表，移除已经过期的访问令牌。
//      * 它会检查访问令牌的过期时间，并从 Redis 中删除过期的记录。
//      * </p>
//      * 
//      * @return 返回被删除的令牌数量
//      */
//     public long doMaintenance() {
//         long removed = 0;
        
//         try (RedisConnection conn = getConnection()) {
//             // 执行 Redis 扫描，查找 client_id_to_access 中的过期访问令牌
//             removed += cleanExpiredTokens(conn, CLIENT_ID_TO_ACCESS);
            
//             // 执行 Redis 扫描，查找 uname_to_access 中的过期访问令牌
//             removed += cleanExpiredTokens(conn, UNAME_TO_ACCESS);
            
//         } catch (Exception e) {
//             // 捕获异常并记录错误日志，避免程序崩溃
//             log.error("Error occurred during Redis maintenance", e);
//             throw new RuntimeException("Error occurred during Redis maintenance", e);  // 将异常抛出，通知调用者
//         }

//         // 返回被删除的令牌数量
//         return removed;
//     }

//     /**
//      * 清理 Redis 中过期的访问令牌
//      * <p>
//      * 该方法通过扫描指定的 Redis 键，移除过期的访问令牌。
//      * </p>
//      * 
//      * @param conn Redis 连接
//      * @param keyPattern 键的模式，例如 `CLIENT_ID_TO_ACCESS` 或 `UNAME_TO_ACCESS`
//      * @return 返回删除的令牌数量
//      */
//     private long cleanExpiredTokens(RedisConnection conn, String keyPattern) {
//         long removed = 0;
        
//         // 执行 Redis 扫描，查找符合模式的键
//         Cursor<byte[]> cursor = conn.scan(ScanOptions.scanOptions()
//                 .match(keyStrResolver.extract(prefix + keyPattern + "*", ":"))
//                 .build());

//         while (cursor.hasNext()) {
//             byte[] key = cursor.next();
//             // 移除过期的访问令牌
//             removed += conn.zRemRangeByScore(key, 0, System.currentTimeMillis());
//         }

//         return removed;
//     }

//     /**
//      * 获取某个客户端和用户的所有访问令牌
//      * <p>
//      * 通过客户端 ID 和用户名，查找并返回所有相关的访问令牌集合。
//      * </p>
//      * @param clientId 客户端 ID
//      * @param userName 用户名
//      * @return OAuth2 访问令牌集合
//      */
//     @Override
//     public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
//         byte[] approvalKey = serializeKey(UNAME_TO_ACCESS + getApprovalKey(clientId, userName));
//         List<byte[]> byteList;
//         try (RedisConnection conn = getConnection()) {
//             byteList = getZByteLists(approvalKey, conn);
//         }
//         if (byteList.isEmpty()) {
//             return Collections.emptySet();
//         }
//         List<OAuth2AccessToken> accessTokens = new ArrayList<>(byteList.size());
//         for (byte[] bytes : byteList) {
//             OAuth2AccessToken accessToken = deserializeAccessToken(bytes);
//             accessTokens.add(accessToken);
//         }
//         return Collections.unmodifiableCollection(accessTokens);
//     }

//     /**
//      * 获取客户端 ID 的所有访问令牌
//      * <p>
//      * 通过客户端 ID 获取所有关联的访问令牌集合。
//      * </p>
//      * @param clientId 客户端 ID
//      * @return OAuth2 访问令牌集合
//      */
//     @Override
//     public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
//         byte[] key = serializeKey(CLIENT_ID_TO_ACCESS + clientId);
//         List<byte[]> byteList;
//         try (RedisConnection conn = getConnection()) {
//             byteList = getZByteLists(key, conn);
//         }
//         if (byteList.isEmpty()) {
//             return Collections.emptySet();
//         }
//         List<OAuth2AccessToken> accessTokens = new ArrayList<>(byteList.size());
//         for (byte[] bytes : byteList) {
//             OAuth2AccessToken accessToken = deserializeAccessToken(bytes);
//             accessTokens.add(accessToken);
//         }
//         return Collections.unmodifiableCollection(accessTokens);
//     }

// }

