package work.metanet.feng.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import work.metanet.feng.admin.api.dto.DepartmentAttributeDTO;
import work.metanet.feng.admin.api.dto.DepartmentOperationDTO;
import work.metanet.feng.admin.api.dto.SysDepartmentTree;
import work.metanet.feng.admin.api.entity.SysDepartment;
import work.metanet.feng.admin.api.entity.SysDeptAttribute;
import work.metanet.feng.admin.api.entity.SysDeptRelation;
import work.metanet.feng.admin.api.entity.SysStaff;
import work.metanet.feng.admin.api.entity.SysUser;
import work.metanet.feng.admin.mapper.SysDepartmentMapper;
import work.metanet.feng.admin.mapper.SysStaffMapper;
import work.metanet.feng.admin.mapper.SysUserMapper;
import work.metanet.feng.admin.service.SysDepartmentService;
import work.metanet.feng.admin.service.SysDeptAttributeService;
import work.metanet.feng.admin.service.SysDeptRelationService;
import work.metanet.feng.admin.service.SysStaffService;
import work.metanet.feng.admin.util.TreeUtil;
import work.metanet.feng.admin.xml.DepartmentXml;
import work.metanet.feng.admin.xml.BatchDepartmentXml;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.data.datascope.DataScope;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 科室表(SysDepartment)表服务实现类
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Service
public class SysDepartmentServiceImpl extends ServiceImpl<SysDepartmentMapper, SysDepartment> implements SysDepartmentService {

    @Autowired
    private  SysDepartmentMapper sysDepartmentMapper;

    @Autowired
    private  SysDeptRelationService sysDeptRelationService;

    @Autowired
    private  SysStaffService sysStaffService;

    @Autowired
    private  SysDeptAttributeService sysDeptAttributeService;
    
    @Autowired
    private SysStaffMapper sysStaffMapper;
    
    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public List<SysDepartmentTree> selectTree(String organCode, String isExtra) {
        // 查询全部部门
//        List<SysDepartment> deptAllList = sysDepartmentMapper.selectList(Wrappers.<SysDepartment>lambdaQuery()
//        		.eq(StrUtil.isNotBlank(organCode), SysDepartment::getOrganCode, organCode)
//        		.orderByDesc(SysDepartment::getSort));
        // 查询数据权限内科室
//        List<Integer> deptOwnIdList = sysDepartmentMapper.selectListByScope(
//        		Wrappers.<SysDepartment>lambdaQuery().like(StrUtil.isNotBlank(organCode), SysDepartment::getOrganCode, organCode), 
//        		DataScope.of()
//        	).stream().map(SysDepartment::getId).collect(Collectors.toList());
        List<SysDepartment> deptScopeList = sysDepartmentMapper.selectListByScope(
        		Wrappers.<SysDepartment>lambdaQuery().like(StrUtil.isNotBlank(organCode), SysDepartment::getOrganCode, organCode), 
        		DataScope.of());
        // 权限内部门
        //return TreeUtil.buildTree(deptAllList, 0);
        return TreeUtil.buildTree(deptScopeList, 0);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public R saveDept(DepartmentAttributeDTO sysDepartment) {
        R r = sysDepartmentCheck(sysDepartment, "0");
        if (r.getCode() != 0) return r;
        this.save(sysDepartment);
        
        // 插入关系
        sysDeptRelationService.insertDeptRelation(sysDepartment);
        
        //新增科室属性表，一个科室对应多个科室属性
        List<String> deptAttributeList = sysDepartment.getDeptAttributeList();
        if (CollectionUtil.isNotEmpty(deptAttributeList)) {
            for (String deptAttribute : deptAttributeList) {
                SysDeptAttribute sysDeptAttribute = new SysDeptAttribute();
                sysDeptAttribute.setDeptId(sysDepartment.getId());
                sysDeptAttribute.setDeptAttribute(deptAttribute);
                sysDeptAttributeService.save(sysDeptAttribute);
            }
        }
        return R.ok();
    }


    /**
     * 更新科室
     *
     * @param sysDepartment 科室信息
     * @return 成功、失败
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public R updateDepartmentById(DepartmentAttributeDTO sysDepartment) {
        R r = sysDepartmentCheck(sysDepartment, "1");
        if (r.getCode() != 0) return r;
        // 更新科室
        this.updateById(sysDepartment);
        
        // 更新关系 
        SysDeptRelation relation = new SysDeptRelation();
        // 改为不允许自己作为自己下级
        // relation.setAncestor(sysDepartment.getId());//默认上级科室为当前科室【一级科室】
        if (StrUtil.isNotBlank(sysDepartment.getParentCode())) {
            // 不是一级科室，查询上级科室
            SysDepartment parentDepartment = this.getOne(Wrappers.<SysDepartment>lambdaQuery()
            		.eq(SysDepartment::getOrganCode, sysDepartment.getOrganCode())
            		.eq(SysDepartment::getDeptCode, sysDepartment.getParentCode())
            		.eq(SysDepartment::getId, sysDepartment.getParentId()));
            if (Objects.nonNull(parentDepartment)) {
                relation.setAncestor(parentDepartment.getId());
                
                // 找到了父节点，所以就地调整关系
                relation.setDescendant(sysDepartment.getId());
                sysDeptRelationService.updateDeptRealtion(relation);
            }
        }
        
        //修改科室属性表，一个科室对应多个科室属性
        List<String> deptAttributeList = sysDepartment.getDeptAttributeList();
        if (CollectionUtil.isNotEmpty(deptAttributeList)) {
            //先删除再新增
            sysDeptAttributeService.remove(Wrappers.<SysDeptAttribute>lambdaQuery().eq(SysDeptAttribute::getDeptId, sysDepartment.getId()));
            for (String deptAttribute : deptAttributeList) {
                SysDeptAttribute sysDeptAttribute = new SysDeptAttribute();
                sysDeptAttribute.setDeptId(sysDepartment.getId());
                sysDeptAttribute.setDeptAttribute(deptAttribute);
                sysDeptAttributeService.save(sysDeptAttribute);
            }
        }
        return R.ok();
    }

    /*
     * @Description: 删除科室，下级科室，关联的科室关系、科室属性、员工、用户
     * @param: id
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public R removeDepartmentById(Integer id) {
        SysDepartment sysDepartment = this.getById(id);
        R r = sysDepartmentCheck(sysDepartment, "2");
        if (r.getCode() != 0) return r;
        // 查询当前所有的子节点，级联删除科室
        List<Integer> descendantIds = sysDeptRelationService.list(Wrappers.<SysDeptRelation>query().lambda()
        		.eq(SysDeptRelation::getAncestor, id))
        		.stream()
        		.map(SysDeptRelation::getDescendant)
        		.collect(Collectors.toList());
        if (CollUtil.isNotEmpty(descendantIds)) {
            //级联删除科室列表
            this.removeByIds(descendantIds);
        }
        //删除无下级的科室
        this.removeById(id);
        // 删除科室关联节点
        sysDeptRelationService.deleteAllDeptRealtion(id);
        // 删除科室关联的属性值
        sysDeptAttributeService.remove(Wrappers.<SysDeptAttribute>lambdaQuery().eq(SysDeptAttribute::getDeptId, id));
        
        // 删除科室关联的人员
        sysStaffMapper.delete(Wrappers.<SysStaff>lambdaQuery().eq(SysStaff::getDeptId, id));
        // 删除科室关联的用户
        sysUserMapper.delete(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getDeptId, id));
        
        return R.ok();
    }

    /**
     * 科室批量操作
     *
     * @param batchDepartmentDTO
     * @return
     */
    @Override
    public R batchDepartmentsSave(DepartmentOperationDTO batchDepartmentDTO, List<String> failMsgList) {
        String organCode = batchDepartmentDTO.getDepartmentDTOList().get(0).getOrganCode();
        for (DepartmentAttributeDTO departmentDTO : batchDepartmentDTO.getDepartmentDTOList()) {
            SysDepartment sysDepartment = sysDepartmentMapper.selectOne(Wrappers.<SysDepartment>lambdaQuery()
            		.eq(SysDepartment::getOrganCode, organCode)
            		.eq(SysDepartment::getDeptCode, departmentDTO.getDeptCode()));
            if ("0".equals(batchDepartmentDTO.getType())) {
                //删除
                if (Objects.isNull(sysDepartment) 
                		&& sysDepartmentMapper.selectCount(Wrappers.<SysDepartment>lambdaQuery()
                				.eq(SysDepartment::getOrganCode, organCode)
                				.eq(SysDepartment::getParentCode, departmentDTO.getDeptCode())
                				.eq(SysDepartment::getParentId, departmentDTO.getId())) > 0 
                		&& sysStaffService.count(Wrappers.<SysStaff>lambdaQuery()
                				.eq(SysStaff::getDeptId, sysDepartment.getId())) > 0) {
                    failMsgList.add("删除科室失败，科室不存在/存在下级科室/有关联人员:" + departmentDTO.getDeptCode());
                    continue;
                }
                //科室存在&没有下级科室&没有关联人员
                this.removeDepartmentById(sysDepartment.getId());
            } else {
                //更新或修改操作
                if (Objects.isNull(sysDepartment)) {
                    //新增操作
                    R r = this.saveDept(departmentDTO);
                    if (r.getCode() != 0) {
                        failMsgList.add(r.getMsg() + ":" + departmentDTO.getDeptCode());
                    }
                } else {
                    //修改操作
                    departmentDTO.setId(sysDepartment.getId());
                    R r = this.updateDepartmentById(departmentDTO);
                    if (r.getCode() != 0) {
                        failMsgList.add(r.getMsg() + ":" + departmentDTO.getDeptCode());
                    }
                }
            }
        }
        return R.ok(failMsgList);
    }

    /**
     * 批量操作科室列表xml
     *
     * @param batchDepartmentXml:
     * @return R
     */
    @Override
    public R batchAddDepartments(BatchDepartmentXml batchDepartmentXml) {
        String type = null;
        List<String> failMsgList = new ArrayList<>();
        //判断操作类型
        DepartmentXml departmentXml1 = batchDepartmentXml.getDepartmentXmlList().get(0);
        if (departmentXml1.getAction().equals("D")) {
            type = "0";//删除
        } else {
            type = "1";//新增或更新
        }
        DepartmentOperationDTO batchDepartmentDTO = new DepartmentOperationDTO();
        List<DepartmentAttributeDTO> list = new ArrayList<>();
        for (DepartmentXml departmentXml : batchDepartmentXml.getDepartmentXmlList()) {
            if (StrUtil.isBlank(departmentXml.getOrganCode())) {
                failMsgList.add("该科室对应机构编码不能为空:" + departmentXml.getDeptCode());
                continue;
            }
            DepartmentAttributeDTO departmentDTO = new DepartmentAttributeDTO();
            List<String> deptAttributeList = new ArrayList<>();
            BeanUtil.copyProperties(departmentXml, departmentDTO, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

            if (departmentXml.getFlagLeaf().equals("Y")) {
                //是否末级部门
                deptAttributeList.add("end_level_department");
            }
            if (StrUtil.isBlank(departmentXml.getParentCode())) {
                //一级科室上级科室编码为空
                departmentDTO.setParentCode("0");
            }
            //保存科室属性
            departmentDTO.setDeptAttributeList(deptAttributeList);
            list.add(departmentDTO);
        }
        batchDepartmentDTO.setType(type);
        batchDepartmentDTO.setDepartmentDTOList(list);
        return batchDepartmentsSave(batchDepartmentDTO, failMsgList);
    }

    /*
     * @Description: 科室新增修改参数校验
     * @param: sysOrgan
     * @param: type 0-新增 1-修改 2-删除
     */
    private R sysDepartmentCheck(SysDepartment sysDepartment, String type) {
    	if (StrUtil.isBlank(sysDepartment.getOrganCode()) || StrUtil.isBlank(sysDepartment.getOrganCode())) {
    		return R.failed("部门名称或者部门代码不能为空");
    	}
    	if (StrUtil.isBlank(sysDepartment.getOrganCode())) {
    		return R.failed("机构代码不能为空");
    	}
    	
        Long count = 0L;
        Long count2 = 0L;
        Long count3 = 0L;
        Long count4 = 0L;

        if (type.equals("0")) {
            //新增机构
            count = baseMapper.selectCount(Wrappers.<SysDepartment>lambdaQuery()
            		.eq(SysDepartment::getOrganCode, sysDepartment.getOrganCode())
            		.eq(SysDepartment::getDeptName, sysDepartment.getDeptName()));
            count2 = baseMapper.selectCount(Wrappers.<SysDepartment>lambdaQuery()
            		.eq(SysDepartment::getOrganCode, sysDepartment.getOrganCode())
            		.eq(SysDepartment::getDeptCode, sysDepartment.getDeptCode()));
        } else if (type.equals("1")) {
            //修改机构
            count = baseMapper.selectCount(Wrappers.<SysDepartment>lambdaQuery()
            		.eq(SysDepartment::getOrganCode, sysDepartment.getOrganCode())
            		.eq(SysDepartment::getDeptName, sysDepartment.getDeptName())
            		.ne(SysDepartment::getId, sysDepartment.getId()));
            count2 = baseMapper.selectCount(Wrappers.<SysDepartment>lambdaQuery()
            		.eq(SysDepartment::getOrganCode, sysDepartment.getOrganCode())
            		.eq(SysDepartment::getDeptCode, sysDepartment.getDeptCode())
            		.ne(SysDepartment::getId, sysDepartment.getId()));
        } else {
            //删除机构,校验是否有下级科室
            count3 = baseMapper.selectCount(Wrappers.<SysDepartment>lambdaQuery()
            		.eq(SysDepartment::getOrganCode, sysDepartment.getOrganCode())
            		.eq(SysDepartment::getParentCode, sysDepartment.getDeptCode()));
            //删除科室，校验科室下的人员是否关联
            count4 = sysStaffService.count(Wrappers.<SysStaff>lambdaQuery()
            		.eq(SysStaff::getOrganCode, sysDepartment.getOrganCode())
            		.eq(SysStaff::getDeptId, sysDepartment.getId()));
        }
        if (count > 0) {
            return R.failed("科室名称不能重复");
        }
        if (count2 > 0) {
            return R.failed("科室编码不能重复");
        }
        if (count3 > 0) {
            //return R.failed("该科室下存在下级科室，不允许删除");
        }
        if (count4 > 0) {
            //return R.failed("该科室下存在关联人员，不允许删除");
        }
        return R.ok();
    }
}