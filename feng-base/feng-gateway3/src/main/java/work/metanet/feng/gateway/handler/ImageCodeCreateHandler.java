package work.metanet.feng.gateway.handler;

import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import work.metanet.feng.common.core.util.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * 验证码生成逻辑处理类 （使用aj验证码）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ImageCodeCreateHandler implements HandlerFunction<ServerResponse> {

	private final ObjectMapper objectMapper;
	
	@Autowired
	private CaptchaService captchaService;

	@Override
//	@SneakyThrows
	public Mono<ServerResponse> handle(ServerRequest serverRequest) {
		try {
			CaptchaVO vo = new CaptchaVO();
			vo.setCaptchaType(serverRequest.queryParam("captchaType").get());
			log.debug("CaptchaVO: {}", vo.toString());
			
//			CaptchaService captchaService = SpringContextHolder.getBean(CaptchaService.class);
			ResponseModel responseModel = captchaService.get(vo);
			
			String bodyData = objectMapper.writeValueAsString(R.ok(responseModel));
			log.debug("bodyData: {}", bodyData);
			
			return ServerResponse.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
					.body(BodyInserters.fromValue(bodyData));
		} catch (Exception e) {
			log.error("创建验证码图片异常，原因：", e.getMessage());
			throw new RuntimeException(e);
		}
	}
}
