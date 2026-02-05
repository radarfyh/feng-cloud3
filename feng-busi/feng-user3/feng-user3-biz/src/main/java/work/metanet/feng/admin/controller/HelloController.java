package work.metanet.feng.admin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@Tag(name = "测试")
public class HelloController {
	@GetMapping("/test")
	public Mono<String> hello() {
		String resp = "Hello, the world!";
		return Mono.just(resp);
	}
}
