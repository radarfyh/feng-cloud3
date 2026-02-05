package work.metanet.feng.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.metanet.feng.admin.api.dto.SysStaffDeptDTO;
import work.metanet.feng.admin.api.entity.SysDepartment;
import work.metanet.feng.admin.api.entity.SysStaff;
import work.metanet.feng.admin.api.entity.SysStaffDept;
import work.metanet.feng.admin.api.vo.SysDepartmentVO;
import work.metanet.feng.admin.mapper.SysStaffDeptMapper;
import work.metanet.feng.admin.service.SysDepartmentService;
import work.metanet.feng.admin.service.SysStaffDeptService;
import work.metanet.feng.admin.service.SysStaffService;
import work.metanet.feng.admin.xml.BatchStaffDeptXml;
import work.metanet.feng.admin.xml.StaffDeptXml;
import work.metanet.feng.common.core.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 人员科室关联表(SysStaffDept)表服务实现类
 *
 * @author edison
 * @since 2022-12-26 09:26:07
 */
@Service
public class SysStaffDeptServiceImpl extends ServiceImpl<SysStaffDeptMapper, SysStaffDept> implements SysStaffDeptService {

    @Autowired
    private SysStaffService sysStaffService;

    @Autowired
    private SysDepartmentService sysDepartmentService;

    @Override
    public List<SysDepartment> getDeptsByStaffId(Integer staffId) {
        return baseMapper.getDeptsByStaffNo(staffId);
    }

    @Override
    public List<SysDepartment> getDeptsByStaffNo(String organCode, String staffNo) {
        SysStaff sysStaff = sysStaffService.getOne(Wrappers.<SysStaff>lambdaQuery().eq(SysStaff::getOrganCode, organCode).eq(SysStaff::getStaffNo, staffNo));
        if (Objects.isNull(sysStaff)) {
            return null;
        }
        return baseMapper.getDeptsByStaffNo(sysStaff.getId());
    }

    @Override
    public R configDept(SysStaffDeptDTO sysStaffDeptDTO) {
        //先删除所有人员关联的科室
        baseMapper.delete(Wrappers.<SysStaffDept>lambdaQuery().eq(SysStaffDept::getStaffId, sysStaffDeptDTO.getStaffId()));
        //再批量新增人员关联科室
        List<SysStaffDept> sysStaffDepts = Arrays.stream(sysStaffDeptDTO.getDeptIds().split(",")).map(id -> {
            SysStaffDept sysStaffDept = new SysStaffDept();
            sysStaffDept.setStaffId(sysStaffDeptDTO.getStaffId());
            sysStaffDept.setDepartmentId(Integer.valueOf(id));
            return sysStaffDept;
        }).collect(Collectors.toList());
        return R.ok(this.saveBatch(sysStaffDepts));
    }

    @Override
    public R addStaffDepts(BatchStaffDeptXml batchStaffDeptXml) {
        List<String> failMsgList = new ArrayList<>();
        List<SysStaffDept> list = new ArrayList<>();
        for (StaffDeptXml staffDeptXml : batchStaffDeptXml.getStaffDeptXmls()) {
            //查询人员信息
            SysStaff sysStaff = sysStaffService.getOne(Wrappers.<SysStaff>lambdaQuery().eq(SysStaff::getOrganCode, staffDeptXml.getOrganCode()).eq(SysStaff::getStaffNo, staffDeptXml.getStaffNo()));
            if (Objects.isNull(sysStaff)) {
                failMsgList.add("该人员不存在:" + staffDeptXml.getStaffNo());
                continue;
            }
            //查询科室信息
            SysDepartment sysDepartment = sysDepartmentService.getOne(Wrappers.<SysDepartment>lambdaQuery().eq(SysDepartment::getOrganCode, staffDeptXml.getOrganCode()).eq(SysDepartment::getDeptCode, staffDeptXml.getDeptCode()));
            if (Objects.isNull(sysDepartment)) {
                failMsgList.add("该科室信息不存在:" + staffDeptXml.getDeptCode());
                continue;
            }
            SysStaffDept sysStaffDept = new SysStaffDept();
            sysStaffDept.setStaffId(sysStaff.getId());
            sysStaffDept.setDepartmentId(sysDepartment.getId());
            list.add(sysStaffDept);
        }
        if (CollectionUtils.isNotEmpty(list)) {
            //先删除所有人员关联的科室
            for (Integer staffId : list.stream().map(SysStaffDept::getStaffId).distinct().collect(Collectors.toList())) {
                baseMapper.delete(Wrappers.<SysStaffDept>lambdaQuery().eq(SysStaffDept::getStaffId, staffId));
            }
            this.saveBatch(list);
        }
        return R.ok(failMsgList);
    }
}
