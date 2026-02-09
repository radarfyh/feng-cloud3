//package ltd.huntinginfo.feng.center.api.vo;
//
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.Data;
//import java.util.Date;
//import java.util.Map;
//
//@Data
//@Schema(name = "接收者映射VO", description = "接收者映射视图对象")
//public class MsgReceiverMappingVO {
//    
//    @Schema(description = "唯一标识UUID")
//    private String id;
//    
//    @Schema(description = "应用标识")
//    private String appKey;
//    
//    @Schema(description = "系统编码")
//    private String sysCode;
//    
//    @Schema(description = "业务系统接收者ID")
//    private String bizReceiverId;
//    
//    @Schema(description = "业务系统接收者名称")
//    private String bizReceiverName;
//    
//    @Schema(description = "接收者类型:USER/ROLE/DEPT/ORG")
//    private String bizReceiverType;
//    
//    @Schema(description = "接收者类型描述")
//    private String bizReceiverTypeDesc;
//    
//    @Schema(description = "部级接收者类型:1-个人 2-单位")
//    private String centerReceiverType;
//    
//    @Schema(description = "部级接收者类型描述")
//    private String centerReceiverTypeDesc;
//    
//    @Schema(description = "接收人证件号码")
//    private String jsrzjhm;
//    
//    @Schema(description = "接收人姓名")
//    private String jsrName;
//    
//    @Schema(description = "接收单位代码")
//    private String jsdwdm;
//    
//    @Schema(description = "接收单位名称")
//    private String jsdwmc;
//    
//    @Schema(description = "状态:0-禁用 1-启用")
//    private Integer status;
//    
//    @Schema(description = "状态描述")
//    private String statusDesc;
//    
//    @Schema(description = "映射类型:STATIC-静态 DYNAMIC-动态")
//    private String mappingType;
//    
//    @Schema(description = "映射类型描述")
//    private String mappingTypeDesc;
//    
//    @Schema(description = "扩展参数")
//    private Object extParams;
//    
//    @Schema(description = "扩展参数Map")
//    private Map<String, Object> extParamsMap;
//    
//    @Schema(description = "创建者")
//    private String createBy;
//    
//    @Schema(description = "创建时间")
//    private Date createTime;
//    
//    @Schema(description = "更新者")
//    private String updateBy;
//    
//    @Schema(description = "修改时间")
//    private Date updateTime;
//    
//    @Schema(description = "接收者映射类型映射")
//    @Data
//    public static class ReceiverTypeMapping {
//        public static final String USER = "USER";      // 用户
//        public static final String ROLE = "ROLE";      // 角色
//        public static final String DEPT = "DEPT";      // 部门
//        public static final String ORG = "ORG";        // 组织
//        
//        public static String getReceiverTypeDesc(String type) {
//            switch (type) {
//                case USER: return "用户";
//                case ROLE: return "角色";
//                case DEPT: return "部门";
//                case ORG: return "组织";
//                default: return "未知";
//            }
//        }
//    }
//    
//    @Schema(description = "部级接收者类型映射")
//    @Data
//    public static class CenterReceiverTypeMapping {
//        public static final String INDIVIDUAL = "1";  // 个人
//        public static final String DEPARTMENT = "2";  // 单位
//        
//        public static String getCenterReceiverTypeDesc(String type) {
//            switch (type) {
//                case INDIVIDUAL: return "个人";
//                case DEPARTMENT: return "单位";
//                default: return "未知";
//            }
//        }
//    }
//    
//    @Schema(description = "映射类型映射")
//    @Data
//    public static class MappingTypeMapping {
//        public static final String STATIC = "STATIC";    // 静态映射
//        public static final String DYNAMIC = "DYNAMIC";  // 动态映射
//        
//        public static String getMappingTypeDesc(String type) {
//            switch (type) {
//                case STATIC: return "静态映射";
//                case DYNAMIC: return "动态映射";
//                default: return "未知";
//            }
//        }
//    }
//    
//    @Schema(description = "状态映射")
//    @Data
//    public static class StatusMapping {
//        public static final Integer DISABLED = 0;  // 禁用
//        public static final Integer ENABLED = 1;   // 启用
//        
//        public static String getStatusDesc(Integer status) {
//            if (status == null) return "未知";
//            switch (status) {
//                case 0: return "禁用";
//                case 1: return "启用";
//                default: return "未知";
//            }
//        }
//    }
//}