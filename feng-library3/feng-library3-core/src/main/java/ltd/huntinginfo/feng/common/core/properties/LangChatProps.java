package ltd.huntinginfo.feng.common.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author tycoding
 * @since 2024/4/15
 */
@Data
@ConfigurationProperties(prefix = "langchat")
public class LangChatProps {
}
