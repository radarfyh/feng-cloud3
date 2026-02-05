package work.metanet.feng.admin.api.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.Data;
import work.metanet.feng.admin.api.entity.SysLog;

@Data
public class SysLogPageDTO {
    private Page<SysLog> page;
    private SysLog sysLog;
}
