package work.metanet.feng.admin.api.vo;

import com.baomidou.mybatisplus.extension.activerecord.Model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
@Schema(description = "查询条件结果")
public class ConditionQueryResultVO  extends Model<ConditionQueryResultVO> {

    private static final long serialVersionUID = 1L;

    @Schema(description = "结果")
    public List<Map<String,String>> result;
    @Schema(description = "数据总数")
    public String total;
}
