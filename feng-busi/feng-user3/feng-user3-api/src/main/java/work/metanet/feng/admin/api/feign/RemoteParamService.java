package work.metanet.feng.admin.api.feign;

import work.metanet.feng.common.core.constant.SecurityConstants;
import work.metanet.feng.common.core.constant.ServiceNameConstants;
import work.metanet.feng.common.core.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @author edison
 * @date 2020/5/12
 * <p>
 * 查询参数相关
 */
@FeignClient(contextId = "remoteParamService", value = ServiceNameConstants.USER2_SERVICE)
public interface RemoteParamService {

	/**
	 * 通过key 查询参数配置
	 * @param key key
	 * @param from 声明成内部调用，避免MQ 等无法调用
	 * @return
	 */
	@GetMapping("/sysPublicParam/publicValue/{key}")
	R<String> getByKey(@PathVariable("key") String key, @RequestHeader(SecurityConstants.FROM) String from);

}
