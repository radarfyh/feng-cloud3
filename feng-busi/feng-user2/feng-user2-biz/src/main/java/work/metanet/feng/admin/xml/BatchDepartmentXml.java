package work.metanet.feng.admin.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

/**
 * @ClassName：BatchDepartmentXml
 * @author edison
 * @Date: 2022/8/1 13:41
 * @Description: BatchDepartmentXml 功能模块
 */
@Data
@JacksonXmlRootElement(localName ="CommonMessage")
public class BatchDepartmentXml {

    @JacksonXmlElementWrapper(localName ="content")
    @JacksonXmlProperty(localName ="entry")
    private List<DepartmentXml> departmentXmlList;


}
