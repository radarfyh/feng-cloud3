package work.metanet.feng.admin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Classname TeamDTO
 * @Date 2023/8/2 11:35
 */
@Data
public class TeamStaffDTO {

    /**
     * 小组id
     */
    @Schema(description = "小组id")
    private Integer teamId;

    /**
     * 人员id集合
     */
    @Schema(description = "人员id集合")
    private List<Integer> staffIds;
}
