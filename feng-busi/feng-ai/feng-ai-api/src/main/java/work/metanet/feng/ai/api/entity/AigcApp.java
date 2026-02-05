package work.metanet.feng.ai.api.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * AI应用程序实体类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Data
@TableName(autoResultMap = true)
@Accessors(chain = true)
@Schema(name = "生成式AI应用",description = "应用表")
public class AigcApp implements Serializable {
    private static final long serialVersionUID = -94917153262781949L;

    /**
     * 应用ID
     */
    @Schema(description = "应用ID")
    private Integer id;
    
    /**
     * 模型ID
     */
    @Schema(description = "模型ID")
    private Integer modelId;

    /**
     * 知识ID集合
     */
    @Schema(description = "知识ID集合")
    @TableField(exist = false)
    private List<Integer> knowledgeIds;
    
    /**
     * 封面
     */
    @Schema(description = "封面")
    private String cover;
    
    /**
     * 名称
     */
    @Schema(description = "名称")
    private String name;

    /**
     * 应用描述
     */
    @Schema(description = "应用描述")
    private String des;

    /**
     * 模型
     */
    @Schema(description = "模型")
    @TableField(exist = false)
    private AigcModel model;
    
    /**
     * 知识
     */
    @Schema(description = "知识")
    @TableField(exist = false)
    private List<AigcKnowledge> knowledges = new ArrayList<>();
    
    /**
     * 状态
     */
    @Schema(description = "状态 1启用 0禁用")
    private String status;
    /**
     * 创建者
     */
    @Schema(description = "创建者")
    private String createBy;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /**
     * 更新者
     */
    @Schema(description = "更新者")
    private String updateBy;
    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
    /**
     * 逻辑删
     */
    @Schema(description = "逻辑删 0-未删除 1-已删除")
    @TableLogic
    private String delFlag;
}
