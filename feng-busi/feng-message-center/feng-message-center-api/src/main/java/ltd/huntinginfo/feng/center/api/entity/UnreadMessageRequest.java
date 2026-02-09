package ltd.huntinginfo.feng.center.api.entity;

import lombok.Data;
import ltd.huntinginfo.feng.center.api.entity.UnreadMessageRequest.QueryPersonInfo;
import ltd.huntinginfo.feng.center.api.entity.UnreadMessageRequest.QueryUnitInfo;

/**
 * 查询未读消息请求
 */
@Data
public class UnreadMessageRequest {
    private QueryUnitInfo cxdwxx;     // 查询单位信息
    private QueryPersonInfo cxrxx;    // 查询人信息
    
    @Data
    public static class QueryUnitInfo {
        private String cxdw;          // 查询单位
        private String cxdwdm;        // 查询单位代码
    }
    
    @Data
    public static class QueryPersonInfo {
        private String cxr;           // 查询人
        private String cxrzjhm;       // 查询人证件号码
    }
}