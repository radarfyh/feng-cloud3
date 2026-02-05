package work.metanet.feng.admin.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

/**
 * @ClassName：DepartmentXml
 * @author edison
 * @Date: 2022/8/1 13:51
 * @Description: 科室 功能模块
 */
@Data
public class DepartmentXml {

    /**
     * A/U:新增/更新 ESB并不知道是新增还是更新操作 D:删除
     */
    @JacksonXmlProperty(localName = "action")
    private String action;

    /**
     * 科室编码
     */
    @JacksonXmlProperty(localName = "DEPTCODE")
    private String deptCode;

    /**
     * 是否末级部门
     */
    @JacksonXmlProperty(localName = "FG_LEAF")
    private String flagLeaf;

    /**
     * 科室名称
     */
    @JacksonXmlProperty(localName = "DEPTNAME")
    private String deptName;

    /**
     * 上级科室编码
     */
    @JacksonXmlProperty(localName = "SUPERIORDEPTCODE")
    private String parentCode;

    /**
     * 科室类别编码
     */
    @JacksonXmlProperty(localName = "DEPTTYPECODE")
    private String deptCategoryCode;

    /**
     * 科室类别名称
     */
    @JacksonXmlProperty(localName = "DEPTTYPENAME")
    private String deptCategoryName;

    /**
     * 分支编码
     */
    @JacksonXmlProperty(localName = "BRANCHCODE")
    private String branchCode;

    /**
     * 分支名称
     */
    @JacksonXmlProperty(localName = "BRANCHNAME")
    private String branchName;

    /**
     * 机构编码
     */
    @JacksonXmlProperty(localName = "PRINCIPALORGCODE")
    private String organCode;

    /**
     * 操作员工号/登记人职工号
     */
    @JacksonXmlProperty(localName = "OPERCODE")
    private String createBy;
}
