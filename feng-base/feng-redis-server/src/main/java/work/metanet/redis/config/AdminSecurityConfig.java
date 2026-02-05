package work.metanet.redis.config;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
public class AdminSecurityConfig {

    private final String adminContextPath;

    public AdminSecurityConfig(AdminServerProperties adminServerProperties) {
        this.adminContextPath = adminServerProperties.getContextPath();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers(adminContextPath + "/assets/**").permitAll()
                .pathMatchers(adminContextPath + "/login").permitAll()
                .anyExchange().authenticated()
            )
            .formLogin(form -> form
                .loginPage(adminContextPath + "/login")
            )
            .logout(logout -> logout
                .logoutUrl(adminContextPath + "/logout")
            )
            .httpBasic(withDefaults())
//            .csrf(csrf -> csrf
//                .csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
//                .requireCsrfProtectionMatcher(exchange -> 
//                    ServerWebExchangeMatchers.pathMatchers(
//                        adminContextPath + "/instances",
//                        adminContextPath + "/actuator/**"
//                    ).matches(exchange)
//                    .flatMap(result -> result.isMatch() 
//                        ? ServerWebExchangeMatcher.MatchResult.notMatch() 
//                        : ServerWebExchangeMatcher.MatchResult.match())
//                )
//            )
            .csrf(csrf -> csrf.disable())
            .build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("radar"))
            .roles("ADMIN")
            .build();
        
        return new MapReactiveUserDetailsService(admin);
    }
}