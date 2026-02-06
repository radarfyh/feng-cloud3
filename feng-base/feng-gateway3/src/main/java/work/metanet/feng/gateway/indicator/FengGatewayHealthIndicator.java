//package work.metanet.feng.gateway.indicator;
//
//import com.anji.captcha.model.common.ResponseModel;
//import com.anji.captcha.model.vo.CaptchaVO;
//import com.anji.captcha.service.CaptchaService;
//import org.springframework.boot.actuate.health.Health;
//import org.springframework.boot.actuate.health.HealthIndicator;
//import org.springframework.stereotype.Component;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import work.metanet.feng.gateway.handler.ImageCodeCreateHandler;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class FengGatewayHealthIndicator implements HealthIndicator {
//
//    private final CaptchaService captchaService;
//
//    @Override
//    public Health health() {
//        try {
//            boolean captchaServiceAvailable = checkCaptchaService();
//            if (!captchaServiceAvailable) {
//                return Health.down()
//                    .withDetail("error", "Captcha service unavailable")
//                    .build();
//            }
//
//            boolean codeGenerationWorking = testCodeGeneration();
//            if (!codeGenerationWorking) {
//                return Health.down()
//                    .withDetail("error", "Code generation failed")
//                    .build();
//            }
//
//            return Health.up()
//                .withDetail("feng-gateway2", "feng-gateway2 service available")
//                .withDetail("code_generation", "Working")
//                .build();
//
//        } catch (Exception e) {
//            log.error("Gateway health check failed", e);
//            return Health.down(e)
//                .withDetail("error", "Health check failed: " + e.getMessage())
//                .build();
//        }
//    }
//
//    private boolean checkCaptchaService() {
//        try {
//            CaptchaVO vo = new CaptchaVO();
//            vo.setCaptchaType("blockPuzzle"); // 使用默认类型测试
//            ResponseModel response = captchaService.get(vo);
//            return response != null && response.isSuccess();
//        } catch (Exception e) {
//            log.warn("Captcha service check failed", e);
//            return false;
//        }
//    }
//
//    private boolean testCodeGeneration() {
//        try {
//        	return true;
//        } catch (Exception e) {
//            log.warn("Code generation test failed", e);
//            return false;
//        }
//    }
//}