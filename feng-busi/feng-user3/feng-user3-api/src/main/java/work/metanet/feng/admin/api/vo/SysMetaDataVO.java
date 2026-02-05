package work.metanet.feng.admin.api.vo;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import work.metanet.feng.admin.api.entity.SysMetaData;
import work.metanet.feng.admin.api.entity.SysMetaDataElement;

@Data
public class SysMetaDataVO extends SysMetaData {
    @Schema(description = "元素对象列表")
    private List<SysMetaDataElement> elements;
}
