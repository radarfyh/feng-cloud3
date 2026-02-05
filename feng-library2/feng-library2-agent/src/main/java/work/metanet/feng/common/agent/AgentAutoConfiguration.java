package work.metanet.feng.common.agent;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import work.metanet.feng.common.core.properties.ChatProps;
import work.metanet.feng.common.core.properties.LangChatProps;

/**
 * aws 自动配置类
 *
 * @author edison
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({
        LangChatProps.class, 
        ChatProps.class,
})
@AllArgsConstructor
public class AgentAutoConfiguration {

}
