package work.metanet.feng.admin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Classname SysAffiliationOrganDTO
 * @Date 2023/8/2 10:36
 */
@Data
public class SysAffiliationOrganDTO {

    /**
     * 联盟id
     */
    @Schema(description = "联盟id")
    private Integer affiliationId;

    /**
     * 机构id集合
     */
    @Schema(description = "机构id集合")
    private List<Integer> organIdList;
}
