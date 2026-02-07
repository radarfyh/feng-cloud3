package ltd.huntinginfo.feng.ai.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import ltd.huntinginfo.feng.ai.api.entity.AigcModel;
import ltd.huntinginfo.feng.common.core.constant.enums.ModelTypeEnum;
import ltd.huntinginfo.feng.common.core.constant.enums.ProviderEnum;

/**
 * 模型DTO类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */
@Data
@Schema(name = "模型DTO",description = "生成式AI模型DTO")
public class ModelDTO {
    private static final long serialVersionUID = 1L;

    @Schema(description = "类型:chat/embedding/image")
    private String type;
    
    @Schema(description = "模型名称")
    private String model;
    
    @Schema(description = "供应商")
    private String provider;
    
    @Schema(description = "名称")
    private String name;
    
    @Schema(description = "状态 1启用 0禁用")
    private String status;
}
