package work.metanet.feng.admin.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

/**
 * @ClassName：BatchStaffDeptXml
 * @author edison
 * @Date: 2022/12/27 10:45
 * @Description: BatchStaffDeptXml 功能模块
 */
@Data
@JacksonXmlRootElement(localName = "CommonMessage")
public class BatchStaffDeptXml {

    @JacksonXmlElementWrapper(localName = "content")
    @JacksonXmlProperty(localName = "entry")
    private List<StaffDeptXml> staffDeptXmls;
}
