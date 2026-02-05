package work.metanet.feng.common.gateway.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import work.metanet.feng.common.core.exception.RouteLoadException;

@ControllerAdvice
@Slf4j
public class RouteExceptionHandler {
    
    @ExceptionHandler(RouteLoadException.class)
    public ResponseEntity<ErrorResult> handleRouteLoadException(RouteLoadException e) {
        log.error("路由加载异常", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResult("ROUTE_LOAD_ERROR", e.getMessage()));
    }
    
    @Data
    @AllArgsConstructor
    public static class ErrorResult {
        private String code;
        private String message;
    }
}
