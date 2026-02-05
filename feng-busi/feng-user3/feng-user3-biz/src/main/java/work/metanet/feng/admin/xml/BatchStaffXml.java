package work.metanet.feng.admin.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

/**
 * @ClassName：BatchStaffXml
 * @author edison
 * @Date: 2022/8/1 21:57
 * @Description: BatchStaffXml 功能模块
 */
@Data
@JacksonXmlRootElement(localName ="CommonMessage")
public class BatchStaffXml {

    @JacksonXmlElementWrapper(localName ="content")
    @JacksonXmlProperty(localName ="entry")
    private List<StaffXml> staffXmlList;


}
