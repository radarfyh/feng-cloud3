package work.metanet.feng.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.metanet.feng.admin.api.entity.SysDepartment;
import lombok.AllArgsConstructor;
import work.metanet.feng.admin.mapper.SysDeptRelationMapper;
import work.metanet.feng.admin.api.entity.SysDeptRelation;
import work.metanet.feng.admin.service.SysDeptRelationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 科室关系表(SysDeptRelation)表服务实现类
 */
@Service
@AllArgsConstructor
public class SysDeptRelationServiceImpl extends ServiceImpl<SysDeptRelationMapper, SysDeptRelation> implements SysDeptRelationService {

    /**
     * 新建科室关系
     */
    @Override
    public void insertDeptRelation(SysDepartment sysDepartment) {
        // 增加科室节点数据
        List<SysDeptRelation> relationList = baseMapper.selectList(
                Wrappers.<SysDeptRelation>query().lambda().eq(SysDeptRelation::getDescendant, sysDepartment.getParentId()))
                .stream().map(relation -> {
                    relation.setDescendant(sysDepartment.getId());
                    return relation;
                }).collect(Collectors.toList());
        if (relationList.size() == 0) {
        	SysDeptRelation relation = new SysDeptRelation();
        	relation.setAncestor(sysDepartment.getParentId());
        	relation.setDescendant(sysDepartment.getId());
        	relationList.add(relation);
        }
        if (CollUtil.isNotEmpty(relationList)) {
            relationList.forEach(ele -> baseMapper.insert(ele));
        }

        // 自己也要维护到关系表中
//        SysDeptRelation own = new SysDeptRelation();
//        own.setDescendant(sysDepartment.getId());
//        own.setAncestor(sysDepartment.getId());
//        baseMapper.insert(own);
    }


    /**
     * 通过deptId删除科室关系
     */
    @Override
    public void deleteAllDeptRealtion(Integer deptId) {
        baseMapper.deleteDeptRelationsByDeptId(deptId);
    }


    /**
     * 更新部门关系
     */
    @Override
    public void updateDeptRealtion(SysDeptRelation relation) {
        baseMapper.deleteDeptRelations(relation);
        baseMapper.insertDeptRelations(relation);
    }
}