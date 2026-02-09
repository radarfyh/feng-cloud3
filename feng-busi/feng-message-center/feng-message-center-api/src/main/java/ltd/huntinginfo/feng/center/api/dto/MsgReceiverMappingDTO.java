//package ltd.huntinginfo.feng.center.api.dto;
//
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.Data;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Size;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
//@Data
//@Schema(name = "接收者映射DTO", description = "接收者映射数据传输对象")
//public class MsgReceiverMappingDTO {
//    
//    @Schema(description = "唯一标识UUID")
//    private String id;
//    
//    @NotBlank(message = "应用标识不能为空")
//    @Size(max = 64, message = "应用标识长度不能超过64个字符")
//    @Schema(description = "应用标识", requiredMode = Schema.RequiredMode.REQUIRED)
//    private String appKey;
//    
//    @NotBlank(message = "系统编码不能为空")
//    @Size(max = 20, message = "系统编码长度不能超过20个字符")
//    @Schema(description = "系统编码", requiredMode = Schema.RequiredMode.REQUIRED)
//    private String sysCode;
//    
//    @NotBlank(message = "业务系统接收者ID不能为空")
//    @Size(max = 50, message = "业务系统接收者ID长度不能超过50个字符")
//    @Schema(description = "业务系统接收者ID", requiredMode = Schema.RequiredMode.REQUIRED)
//    private String bizReceiverId;
//    
//    @NotBlank(message = "业务系统接收者名称不能为空")
//    @Size(max = 100, message = "业务系统接收者名称长度不能超过100个字符")
//    @Schema(description = "业务系统接收者名称", requiredMode = Schema.RequiredMode.REQUIRED)
//    private String bizReceiverName;
//    
//    @Size(max = 10, message = "接收者类型长度不能超过10个字符")
//    @Schema(description = "接收者类型:USER/ROLE/DEPT/ORG", defaultValue = "USER")
//    private String bizReceiverType;
//    
//    @NotBlank(message = "部级接收者类型不能为空")
//    @Size(max = 10, message = "部级接收者类型长度不能超过10个字符")
//    @Schema(description = "部级接收者类型:1-个人 2-单位", requiredMode = Schema.RequiredMode.REQUIRED)
//    private String centerReceiverType;
//    
//    @Size(max = 18, message = "接收人证件号码长度不能超过18个字符")
//    @Schema(description = "接收人证件号码(个人时必填)")
//    private String jsrzjhm;
//    
//    @Size(max = 100, message = "接收人姓名长度不能超过100个字符")
//    @Schema(description = "接收人姓名(个人时必填)")
//    private String jsrName;
//    
//    @Size(max = 12, message = "接收单位代码长度不能超过12个字符")
//    @Schema(description = "接收单位代码(单位时必填)")
//    private String jsdwdm;
//    
//    @Size(max = 200, message = "接收单位名称长度不能超过200个字符")
//    @Schema(description = "接收单位名称(单位时必填)")
//    private String jsdwmc;
//    
//    @Schema(description = "状态:0-禁用 1-启用", defaultValue = "1")
//    private Integer status;
//    
//    @Size(max = 20, message = "映射类型长度不能超过20个字符")
//    @Schema(description = "映射类型:STATIC-静态 DYNAMIC-动态", defaultValue = "STATIC")
//    private String mappingType;
//    
//    @Schema(description = "扩展参数")
//    private Object extParams;
//    
//    @Schema(description = "创建者")
//    private String createBy;
//    
//    @Schema(description = "更新者")
//    private String updateBy;
//    
//    // 查询条件字段
//    @Schema(description = "接收者类型列表")
//    private List<String> bizReceiverTypeList;
//    
//    @Schema(description = "部级接收者类型列表")
//    private List<String> centerReceiverTypeList;
//    
//    @Schema(description = "状态列表")
//    private List<Integer> statusList;
//    
//    @Schema(description = "映射类型列表")
//    private List<String> mappingTypeList;
//    
//    @Schema(description = "排序字段")
//    private String sortField;
//    
//    @Schema(description = "排序方向")
//    private String sortDirection;
//    
//    @Schema(description = "创建时间查询开始")
//    private Date createTimeStart;
//    
//    @Schema(description = "创建时间查询结束")
//    private Date createTimeEnd;
//    
//    @Schema(description = "是否包含扩展参数")
//    private Boolean hasExtParams;
//    
//    @Schema(description = "模糊查询关键词")
//    private String keyword;
//}