package work.metanet.feng.admin.api.dto;/**
 * @ClassName: StaffDTO
 * @Date: 2022/7/28 18:50
 * @author edison
 */

import work.metanet.feng.admin.api.entity.SysStaff;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 *@ClassName StaffDTO
 *@author edison
 *@Date 2022/7/28 18:50
 **/
@Data
@Schema(description = "人员传输DTO")
public class StaffDTO {

    /**
     * 操作类型：0-删除 1-新增/修改
     */
    @Schema(description = "操作类型：0-删除 1-新增 2-修改")
    private String type;

    /**
     * 人员集合
     */
    @Schema(description = "人员集合")
    private List<SysStaff> sysStaffList;
}
