package work.metanet.feng.admin.api.feign;

import work.metanet.feng.admin.api.entity.SysDatasource;
import work.metanet.feng.common.core.constant.SecurityConstants;
import work.metanet.feng.common.core.constant.ServiceNameConstants;
import work.metanet.feng.common.core.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @ClassName: RemoteDataSourceService
 * @Description: 数据源feign接口
 * @Date: 2022/6/20 10:13
 * @author edison
 */
@FeignClient(contextId = "remoteDataSourceService", value = ServiceNameConstants.USER2_SERVICE)
public interface RemoteDataSourceService {

    /**
     * 根据数据源id查详情
     *
     * @param id:
     * @param from:
     * @return R
     */
    @GetMapping("/sysDatasource/{id}")
    R<SysDatasource> selectOne(@PathVariable("id") Integer id, @RequestHeader(SecurityConstants.FROM) String from);
}
