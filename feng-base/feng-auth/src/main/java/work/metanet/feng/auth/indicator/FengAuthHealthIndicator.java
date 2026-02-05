//package work.metanet.feng.auth.indicator;
//
//import org.springframework.boot.actuate.health.Health;
//import org.springframework.boot.actuate.health.HealthIndicator;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//import org.springframework.stereotype.Component;
//
//@Component
//public class FengAuthHealthIndicator implements HealthIndicator {
//
//    private final TokenStore tokenStore;
//    
//    public FengAuthHealthIndicator(TokenStore tokenStore) {
//        this.tokenStore = tokenStore;
//    }
//
//    @Override
//    public Health health() {
//        try {
//            // 查询当前存储的有效令牌数量
//            int tokenCount = tokenStore.findTokensByClientId("default-client").size();
//            
//            return tokenCount > 0 
//                ? Health.up().withDetail("token_count", tokenCount).build()
//                : Health.down().withDetail("error", "No active tokens found").build();
//                
//        } catch (Exception e) {
//            return Health.down()
//                .withException(e)
//                .withDetail("error", "Token store unavailable: " + e.getMessage())
//                .build();
//        }
//    }
//}