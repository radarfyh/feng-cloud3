package work.metanet.feng.admin.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

/**
 * @ClassName：StaffDeptXml
 * @author edison
 * @Date: 2022/12/27 10:48
 * @Description: StaffDeptXml 功能模块
 */
@Data
public class StaffDeptXml {

    /**
     * A/U:新增/更新 ESB并不知道是新增还是更新操作 D:删除
     */
    @JacksonXmlProperty(localName = "action")
    private String action;

    /**
     * 机构编码
     */
    @JacksonXmlProperty(localName = "ORG_CODE")
    private String organCode;

    /**
     * 员工代码
     */
    @JacksonXmlProperty(localName = "YSCODE")
    private String staffNo;

    /**
     * 科室编码
     */
    @JacksonXmlProperty(localName = "KSCODE")
    private String deptCode;
}
