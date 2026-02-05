package work.metanet.feng.common.gateway.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.cloud.gateway.route.RouteDefinition;

import java.io.Serializable;

/**
 * @author edison
 * @date 2021/1/15
 * <p>
 * 扩展此类支持序列化
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RouteDefinitionVo extends RouteDefinition implements Serializable {

	/**
	 * 路由名称
	 */
	private String routeName;
	/**
	 * 路由ID
	 */	
	private String routeId;
}
