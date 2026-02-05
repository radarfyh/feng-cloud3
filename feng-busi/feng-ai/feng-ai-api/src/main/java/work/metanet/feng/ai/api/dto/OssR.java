package work.metanet.feng.ai.api.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

/**
 * OSS 文件对象
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Data
@Accessors(chain = true)
@Schema(name = "对象存储请求",description = "对象存储请求DTO")
public class OssR implements Serializable {
    private static final long serialVersionUID = 5117927170776709434L;
    
    @Schema(description = "对象存储ID for 对象存储系统")
    private String ossId;
    
    @Schema(description = "文件ID for 操作系统")
    private String fileId;
    
    @Schema(description = "链接", requiredMode = RequiredMode.REQUIRED)
    private String url;
    
    @Schema(description = "大小")
    private Long size;
    
    @Schema(description = "文件名称")
    private String filename;
    
    @Schema(description = "原始文件名称", requiredMode = RequiredMode.REQUIRED)
    private String originalFilename;
    
    @Schema(description = "基准路径")
    private String basePath;
    @Schema(description = "路径")
    private String path;
    @Schema(description = "扩展")
    private String ext;
    @Schema(description = "内容类型")
    private String contentType;
    @Schema(description = "平台")
    private String platform;
}
