package work.metanet.feng.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.metanet.feng.admin.api.entity.SysDepartment;
import work.metanet.feng.admin.api.entity.SysDeptRelation;

/**
 * 科室关系表(SysDeptRelation)表服务接口
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
public interface SysDeptRelationService extends IService<SysDeptRelation> {

    /*
     *
     * @Description:新建科室关系
     * @author edison
     * @date 2022/5/17
     * @param: sysDepartment
     * @return
     */
    void insertDeptRelation(SysDepartment sysDepartment);

    /**
     * 通过deptId删除科室关系
     * @param deptId
     */
    void deleteAllDeptRealtion(Integer deptId);

    /*
     *
     * @Description:更新部门关系
     * @author edison
     * @date 2022/5/17
     * @param: relation
     * @return
     */
    void updateDeptRealtion(SysDeptRelation relation);
}