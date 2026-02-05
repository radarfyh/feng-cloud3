package work.metanet.feng.admin.api.dto;

import work.metanet.feng.admin.api.entity.SysDatasource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Classname SysDatasourceDTO
 * @Date 2023/5/25 14:30
 */
@Data
public class SysDatasourceDTO extends SysDatasource {

    /**
     * 密码是否加密
     */
    @Schema(description = "密码是否加密，默认不加密")
    private Boolean isEncrypt = false;
}
