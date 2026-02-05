package work.metanet.feng.ai.config;

import static dev.langchain4j.internal.RetryUtils.withRetry;
import static dev.langchain4j.internal.Utils.getOrDefault;
import static dev.langchain4j.spi.ServiceHelper.loadFactories;
import static java.util.stream.Collectors.toList;

import java.net.Proxy;
import java.util.List;
import java.util.Optional;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.internal.Utils;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.model.qianfan.QianfanEmbeddingModel;
import dev.langchain4j.model.qianfan.QianfanEmbeddingModelNameEnum;
import dev.langchain4j.model.qianfan.client.QianfanClient;
import dev.langchain4j.model.qianfan.client.embedding.EmbeddingRequest;
import dev.langchain4j.model.qianfan.client.embedding.EmbeddingResponse;
import work.metanet.feng.common.core.constant.enums.BusinessEnum;
import work.metanet.feng.common.core.exception.BusinessException;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FengQianfanEmbeddingModel extends QianfanEmbeddingModel {
    private final QianfanClient client;
    private final String baseUrl;
    private final String modelName;
    private final Integer maxRetries;
    private final String user;
    private final String endpoint;
    
    @Builder
	public FengQianfanEmbeddingModel(String baseUrl, String apiKey, String secretKey, Integer maxRetries,
			String modelName, String endpoint, String user, Boolean logRequests, Boolean logResponses, Proxy proxy) {
		super(baseUrl, apiKey, secretKey, maxRetries, modelName, endpoint, user, logRequests, logResponses, proxy);
        if (Utils.isNullOrBlank(apiKey) || Utils.isNullOrBlank(secretKey)) {
            throw new BusinessException(BusinessEnum.WEB_INTERNAL_SERVER_ERROR.getCode(), "API key和secret key必须配置");
        }

        this.modelName = modelName;
        this.endpoint = Utils.isNullOrBlank(endpoint) ? QianfanEmbeddingModelNameEnum.getEndpoint(modelName) : endpoint;

        if (Utils.isNullOrBlank(this.endpoint)) {
            throw new BusinessException(BusinessEnum.WEB_INTERNAL_SERVER_ERROR.getCode(), "不支持的千帆模型名称，请参考：https://cloud.baidu.com/doc/WENXINWORKSHOP/s/Nlks5zkzu");
        }

        this.baseUrl = getOrDefault(baseUrl, "https://aip.baidubce.com");
        this.client = QianfanClient.builder()
                .baseUrl(this.baseUrl)
                .apiKey(apiKey)
                .secretKey(secretKey)
                .logRequests(logRequests)
                .logResponses(logResponses)
                .proxy(proxy)
                .build();
        this.maxRetries = getOrDefault(maxRetries, 3);
        this.user = user;

	}

    @Override
    public Response<List<Embedding>> embedAll(List<TextSegment> textSegments) {
    	try {
            List<String> texts = textSegments.stream()
                    .map(TextSegment::text)
                    .collect(toList());
            if (texts.stream().anyMatch(text -> text == null || text.isEmpty())) {
                throw new BusinessException(BusinessEnum.WEB_INTERNAL_SERVER_ERROR.getCode(), "输入文本不能为空");
            }
            
            Response<List<Embedding>> resp = embedTexts(texts);
	    	return resp;
    	} catch (Exception e) {
    		log.error("千帆向量化服务异常", e);
    		throw new BusinessException(BusinessEnum.WEB_INTERNAL_SERVER_ERROR.getCode(), "千帆服务处理失败：" + e.getMessage());
    	}
    }
    
    private Response<List<Embedding>> embedTexts(List<String> texts) {
    	try {
	        EmbeddingRequest request = EmbeddingRequest.builder()
	                .input(texts)
	                .model(modelName)
	                .user(user)
	                .build();
	
	        EmbeddingResponse response = withRetry(() -> client.embedding(request, endpoint).execute(), maxRetries);
	        if (response == null) {
	            throw new BusinessException(BusinessEnum.WEB_INTERNAL_SERVER_ERROR.getCode(), "千帆API返回空响应体");
	        }
            // 特殊处理336003错误码
            if ("336003".equals(response.getErrorCode())) {
                throw new BusinessException(BusinessEnum.WEB_INTERNAL_SERVER_ERROR.getCode(),  "令牌数量太大，请减少提示语或者缩短消息长度！");
            }
	        // 检查业务错误码
	        if (response.getErrorCode() != null) {
	            throw new BusinessException(BusinessEnum.WEB_INTERNAL_SERVER_ERROR.getCode(), String.format("千帆API错误[%s]: %s", 
	                response.getErrorCode(), response.getErrorMsg()));
	        }
	        if (response.data() == null || response.data().isEmpty()) {
	            throw new BusinessException(BusinessEnum.WEB_INTERNAL_SERVER_ERROR.getCode(), "千帆API返回空特征数据");
	        }
	        List<Embedding> embeddings = response.data().stream()
	                .map(openAiEmbedding -> {
	                	if (openAiEmbedding.embedding() == null) {
	                        throw new BusinessException(BusinessEnum.WEB_INTERNAL_SERVER_ERROR.getCode(), "特征数据解析异常");
	                    }
	                	return Embedding.from(openAiEmbedding.embedding());
	                })
	                .collect(toList());
	
	        return Response.from(
	                embeddings,
	                tokenUsageFrom(response)
	        );
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("千帆向量化处理异常", e);
            throw new BusinessException(BusinessEnum.WEB_INTERNAL_SERVER_ERROR.getCode(), "特征提取失败：" + e.getMessage());
        }
    }
    
    static TokenUsage tokenUsageFrom(EmbeddingResponse response) {
        return Optional.of(response)
                .map(EmbeddingResponse::getUsage)
                .map(usage -> new TokenUsage(usage.promptTokens(), 
                		usage.completionTokens(), 
                		usage.totalTokens()))
                .orElse(null);
    }
    
    public static FengQianfanEmbeddingModelBuilder builder() {
        for (FengQianfanEmbeddingModelBuilderFactory factory : loadFactories(FengQianfanEmbeddingModelBuilderFactory.class)) {
            return factory.get();
        }
        return new FengQianfanEmbeddingModelBuilder();
    }
    
    public static class FengQianfanEmbeddingModelBuilder extends QianfanEmbeddingModelBuilder {
    }
}
