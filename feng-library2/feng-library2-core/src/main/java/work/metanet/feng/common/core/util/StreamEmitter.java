package work.metanet.feng.common.core.util;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;

@Slf4j
public class StreamEmitter {

    private final SseEmitter emitter;

    public StreamEmitter() {
        emitter = new SseEmitter(50 * 60 * 1000L);
    }

    public SseEmitter get() {
        return emitter;
    }

    public SseEmitter streaming(final ExecutorService executor, Runnable func) {
        emitter.onCompletion(() -> {
            log.debug("streaming-->收到onCompletion事件， executor即将关闭");
            
            executor.shutdownNow();
        });

        emitter.onError((e) -> {
            log.error("streaming-->收到onError事件：{}， executor即将关闭", e.getMessage());
            
            executor.shutdownNow();
        });

        emitter.onTimeout(() -> {
            log.error("streaming-->收到onTimeout事件， executor即将关闭");
            executor.shutdownNow();
        });
        
        executor.execute(() -> {
            try {
                func.run();
            } catch (Exception e) {
                log.error("streaming-->executor.execute-->捕获到异常: {}", e.getMessage());
                emitter.completeWithError(e);
                Thread.currentThread().interrupt();
            } finally {
                if (!executor.isShutdown()) {
                	log.debug("streaming-->executor.execute--> executor即将关闭");
                    executor.shutdownNow();
                }
            }
        });
        return emitter;
    }

    public void send(Object obj) {
        try {
        	emitter.send(obj);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void complete() {
        emitter.complete();
    }

    public void error(String message) {
        try {
        	log.error("Error: " + message);
        	emitter.send("Error: " + message);  
        	emitter.complete();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
