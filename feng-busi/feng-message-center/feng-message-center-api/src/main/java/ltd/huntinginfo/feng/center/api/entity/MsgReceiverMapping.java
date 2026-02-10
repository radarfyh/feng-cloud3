//package ltd.huntinginfo.feng.center.api.entity;
//
//import com.baomidou.mybatisplus.annotation.*;
//import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.Data;
//import java.io.Serializable;
//import java.util.Date;
//
//@Data
//@TableName(value = "msg_receiver_mapping", autoResultMap = true)
//@Schema(name = "接收者映射", description = "接收者映射实体，记录业务系统接收者与部级接收者的映射关系")
//public class MsgReceiverMapping implements Serializable {
//    private static final long serialVersionUID = 1L;
//
//    @TableId
//    @Schema(description = "唯一标识UUID", requiredMode = Schema.RequiredMode.REQUIRED)
//    private String id;
//
//    @Schema(description = "应用标识", requiredMode = Schema.RequiredMode.REQUIRED)
//    private String appKey;
//    
//    @Schema(description = "业务系统接收者ID", requiredMode = Schema.RequiredMode.REQUIRED)
//    private String bizReceiverId;
//    
//    @Schema(description = "业务系统接收者名称", requiredMode = Schema.RequiredMode.REQUIRED)
//    private String bizReceiverName;
//    
//    @Schema(description = "接收者类型:USER/ROLE/DEPT/ORG", defaultValue = "USER")
//    private String bizReceiverType;
//    
//    @Schema(description = "部级接收者类型:1-个人 2-单位", requiredMode = Schema.RequiredMode.REQUIRED)
//    private String centerReceiverType;
//    
//    @Schema(description = "接收人证件号码(个人时必填)")
//    private String jsrzjhm;
//    
//    @Schema(description = "接收人姓名(个人时必填)")
//    private String jsrName;
//    
//    @Schema(description = "接收单位代码(单位时必填)")
//    private String jsdwdm;
//    
//    @Schema(description = "接收单位名称(单位时必填)")
//    private String jsdwmc;
//    
//    @Schema(description = "状态:0-禁用 1-启用", defaultValue = "1")
//    private Integer status;
//    
//    @Schema(description = "映射类型:STATIC-静态 DYNAMIC-动态", defaultValue = "STATIC")
//    private String mappingType;
//    
//    @TableField(typeHandler = JacksonTypeHandler.class)
//    @Schema(description = "扩展参数")
//    private Object extParams;
//
//    @TableField(fill = FieldFill.INSERT)
//    @Schema(description = "创建者")
//    private String createBy;
//
//    @TableField(fill = FieldFill.INSERT)
//    @Schema(description = "创建时间")
//    private Date createTime;
//
//    @TableField(fill = FieldFill.UPDATE)
//    @Schema(description = "更新者")
//    private String updateBy;
//
//    @TableField(fill = FieldFill.UPDATE)
//    @Schema(description = "修改时间")
//    private Date updateTime;
//
//    @TableLogic
//    @Schema(description = "逻辑删 0-正常 1-删除", defaultValue = "0")
//    private String delFlag;
//}