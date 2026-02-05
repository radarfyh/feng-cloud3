package work.metanet.feng.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import work.metanet.feng.admin.api.entity.SysDeptRelation;
import work.metanet.feng.common.data.datascope.FengBaseMapper;

/**
 * 科室关系表(SysDeptRelation)表数据库访问层
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Mapper
public interface SysDeptRelationMapper extends FengBaseMapper<SysDeptRelation> {

    /**
     * 删除部门 > 删除所有关联此科室子节点的闭包关系
     * @param deptId 科室id
     */
    void deleteDeptRelationsByDeptId(Integer deptId);

    /**
     * 删除节点数据
     * @param deptRelation 关系节点
     */
    void deleteDeptRelations(SysDeptRelation deptRelation);

    /**
     * 新增节点数据
     * @param deptRelation 关系节点
     */
    void insertDeptRelations(SysDeptRelation deptRelation);
}