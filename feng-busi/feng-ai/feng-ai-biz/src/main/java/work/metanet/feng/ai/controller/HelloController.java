package work.metanet.feng.ai.controller;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 测试控制类（APIs）
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@RequestMapping("/test")
@Slf4j
@Tag(name = "测试模块")
public class HelloController {
	@GetMapping("/hello")
	@Operation(summary = "say hello测试")
	public Mono<String> hello() {
		String resp = "Hello, the world!";
		return Mono.just(resp);
	}
	
	@Operation(summary = "模拟流")
    @GetMapping(value = "/streamByMvc", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamEventsByMvc() {
        log.info("SSE stream requested"); // 打印日志
        SseEmitter emitter = new SseEmitter();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                for (int i = 1; i <= 100; i++) {
                	String msg = "SSE Event - " + i;
                    emitter.send(SseEmitter.event()
                            .data(msg)
                            .id(String.valueOf(i))
                            .name("sse-event"));
                    log.info("SSE stream is sent: {}", msg);
                    Thread.sleep(1000); // 模拟延迟
                }
                emitter.complete(); // 完成 SSE 流
            } catch (IOException | InterruptedException e) {
                emitter.completeWithError(e); // 发生错误时完成流
            }
        });

        return emitter;
    }
    
	@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@Operation(summary = "模拟Flux流")
	public Flux<ServerSentEvent<String>> streamEvents() {
	    return Flux.interval(Duration.ofSeconds(1)) // 每秒发送一个事件
	            .map(sequence -> ServerSentEvent.<String>builder()
	                    .id(String.valueOf(sequence)) // 事件 ID
	                    .event("sse-event") // 事件名称
	                    .data("SSE Event - " + LocalTime.now()) // 事件数据
	                    .build())
	            .take(100) // 限制发送 100 条消息
	            .doOnComplete(() -> log.info("SSE stream completed")) // 流完成时打印日志
	            .doFinally(signalType -> log.info("SSE stream terminated with signal: {}", signalType)); // 流终止时打印日志
	}
}
