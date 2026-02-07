package ltd.huntinginfo.feng.ai.api.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ltd.huntinginfo.feng.common.core.constant.enums.EmbedStoreEnum;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI使用的向量库实体类
 * 用于存储切片信息到本地，方便使用向量算法查询关键词相关内容
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Data
@TableName(autoResultMap = true)
@Schema(name = "生成式AI向量库",description = "向量库表")
public class AigcEmbedStore implements Serializable {
    private static final long serialVersionUID = 548724967827903685L;

    /**
     * 主键，向量库ID
     */
    @Schema(description = "向量库ID")
    private Integer id;
    
    /**
     * 向量库名称
     */
    @Schema(description = "向量库名称")
    private String name;

    /**
     * 向量数据库类型
     */
    @Schema(description = "向量数据库类型")
    private String provider;
    
    /**
     * 主机地址
     */
    @Schema(description = "主机地址")
    private String host;
    
    /**
     * 主机端口
     */
    @Schema(description = "主机端口")
    private Integer port;
    
    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;
    
    /**
     * 密码
     */
    @Schema(description = "密码")
    private String password;
    
    /**
     * 数据库名称
     */
    @Schema(description = "数据库名称")
    private String databaseName;
    
    /**
     * 表名
     */
    @Schema(description = "表名")
    private String tableName;
    
    /**
     * 向量维度
     */
    @Schema(description = "向量维度")
    private Integer dimension;

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
