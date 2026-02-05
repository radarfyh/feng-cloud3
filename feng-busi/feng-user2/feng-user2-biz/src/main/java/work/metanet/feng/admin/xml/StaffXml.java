package work.metanet.feng.admin.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import work.metanet.feng.common.core.constant.enums.Gender;

/**
 * @ClassName：StaffXml
 * @author edison
 * @Date: 2022/8/1 21:59
 * @Description: 人员 功能模块
 */
@Data
public class StaffXml {

    /**
     * A/U:新增/更新 ESB并不知道是新增还是更新操作 D:删除
     */
    @JacksonXmlProperty(localName = "action")
    private String action;

    /**
     * 员工代码
     */
    @JacksonXmlProperty(localName = "EMPCODE")
    private String staffNo;
    /**
     * 员工姓名
     */
    @JacksonXmlProperty(localName = "EMPNAME")
    private String staffName;
    /**
     * 性别代码
     */
    @JacksonXmlProperty(localName = "GENDERCODE")
    private Gender genderCode;
    /**
     * 身份证号
     */
    @JacksonXmlProperty(localName = "IDNO")
    private String identificationNo;
    /**
     * 出生日期
     */
    @JacksonXmlProperty(localName = "BIRTHDAY")
    private String birthdate;
    /**
     * 专业技术职务代码
     */
    @JacksonXmlProperty(localName = "TECDUTYCODE")
    private String technicalQualificationsCode;
    /**
     * 专业技术职务名称
     */
    @JacksonXmlProperty(localName = "TECDUTYNAME")
    private String technicalQualificationsName;
    /**
     * 科室代码
     */
    @JacksonXmlProperty(localName = "DEPTCODE")
    private String deptCode;
    /**
     * 机构编码
     */
    @JacksonXmlProperty(localName = "ORGANCODE")
    private String organCode;

}
