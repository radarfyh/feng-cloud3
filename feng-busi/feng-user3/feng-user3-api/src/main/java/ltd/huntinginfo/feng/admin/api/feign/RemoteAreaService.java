package ltd.huntinginfo.feng.admin.api.feign;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import ltd.huntinginfo.feng.common.core.constant.ServiceNameConstants;
import ltd.huntinginfo.feng.common.core.util.R;
import ltd.huntinginfo.feng.common.feign.annotation.NoToken;

/**
 * 区域服务Feign客户端
 */
@FeignClient(contextId = "remoteAreaService", value = ServiceNameConstants.USER3_SERVICE)
public interface RemoteAreaService {

    @NoToken
    @GetMapping("/administrative-division/code/{areaCode}")
    R<Map<String, Object>> getAreaByCode(@PathVariable String areaCode);

    @NoToken
    @PostMapping("/administrative-division/list-by-codes")
    R<List<Map<String, Object>>> listAreasByCodes(@RequestBody List<String> areaCodes);

    @NoToken
    @PostMapping("/administrative-division/users")
    R<List<Map<String, Object>>> listUsersByArea(@RequestBody Map<String, Object> query);
}
