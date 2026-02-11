package ltd.huntinginfo.feng.admin.service.dict;

import ltd.huntinginfo.feng.admin.api.entity.dict.DictAdministrativeDivision;

import java.util.List;
import java.util.Map;

/**
 * 行政区划代码表 服务接口(GB/T 2260-2013)
 */
public interface DictAdministrativeDivisionService extends BaseDictService<DictAdministrativeDivision> {
    
    /**
     * 根据父级代码获取子行政区划
     */
    List<DictAdministrativeDivision> getByParentCode(String parentCode);
    
    /**
     * 根据行政级别获取行政区划
     */
    List<DictAdministrativeDivision> getByLevel(Integer level);
    
    /**
     * 获取行政区划树（省-市-县）
     */
    List<DictAdministrativeDivision> getDivisionTree();
    
    /**
     * 根据多个区域编码批量查询行政区划信息
     */
   List<Map<String, Object>> listAreasByCodes(List<String> areaCodes);

    /**
     * 根据区域查询下属用户信息
     */
    List<Map<String, Object>> listUsersByArea(Map<String, Object> query);
}
