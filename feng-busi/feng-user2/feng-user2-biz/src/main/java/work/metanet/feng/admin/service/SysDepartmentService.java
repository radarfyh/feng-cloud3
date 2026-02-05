package work.metanet.feng.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;

import work.metanet.feng.admin.api.dto.DepartmentAttributeDTO;
import work.metanet.feng.admin.api.dto.DepartmentOperationDTO;
import work.metanet.feng.admin.api.dto.SysDepartmentTree;
import work.metanet.feng.admin.api.entity.SysDepartment;
import work.metanet.feng.admin.api.vo.SysDepartmentVO;
import work.metanet.feng.admin.xml.BatchDepartmentXml;
import work.metanet.feng.common.core.util.R;

import java.util.List;

/**
 * 科室表(SysDepartment)表服务接口
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
public interface SysDepartmentService extends IService<SysDepartment> {

    /*
     *
     * @Description:根据机构编码查询科室菜单树
     * @author edison
     * @date 2022/5/17
     * @param: organCode
     * @return
     */
    List<SysDepartmentTree> selectTree(String organCode, String isExtra);

    /*
     *
     * @Description: 新增科室
     * @author edison
     * @date 2022/5/17
     * @param: sysDepartment
     * @return
     */
    R saveDept(DepartmentAttributeDTO departmentDTO);

    /*
     *
     * @Description: 编辑科室
     * @author edison
     * @date 2022/5/17
     * @param: sysDepartment
     * @return
     */
    R updateDepartmentById(DepartmentAttributeDTO departmentDTO);

    /*
     *
     * @Description: 删除科室
     * @author edison
     * @date 2022/5/17
     * @param: idList
     * @return
     */
    R removeDepartmentById(Integer id);

    /**
     * 科室批量操作
     *
     * @param departmentOperationDTO
     * @return
     */
    R batchDepartmentsSave(DepartmentOperationDTO departmentOperationDTO,List<String> failMsgList);

    /**
     * 批量操作科室列表
     *
     * @param esbDepartmentXml:
     * @return R
     */
    R batchAddDepartments(BatchDepartmentXml esbDepartmentXml);


}