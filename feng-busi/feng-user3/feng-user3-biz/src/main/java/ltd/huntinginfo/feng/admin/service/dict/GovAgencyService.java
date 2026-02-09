package ltd.huntinginfo.feng.admin.service.dict;

import ltd.huntinginfo.feng.admin.api.entity.dict.GovAgency;

import java.util.List;

/**
 * 政府机关代码表 服务接口
 */
public interface GovAgencyService extends BaseDictService<GovAgency> {
    
    /**
     * 根据父级代码获取子机构
     */
    List<GovAgency> getByParentCode(String parentCode);
    
    /**
     * 根据机构级别获取机构列表
     */
    List<GovAgency> getByLevel(Integer level);
    
    /**
     * 获取机构树形结构
     */
    List<GovAgency> getAgencyTree();
}
