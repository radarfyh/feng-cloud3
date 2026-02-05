package work.metanet.feng.admin.service.impl;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.StrUtil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import work.metanet.feng.admin.api.entity.PrmContact;
import work.metanet.feng.admin.api.entity.SysDepartment;
import work.metanet.feng.admin.api.entity.SysOrgan;
import work.metanet.feng.admin.mapper.SysOrganMapper;
import work.metanet.feng.admin.service.SysDepartmentService;
import work.metanet.feng.admin.service.SysOrganService;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.core.constant.enums.BuiltInRoleEnum;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.security.util.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 机构表(SysOrgan)表服务实现类
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Service
@AllArgsConstructor
public class SysOrganServiceImpl extends ServiceImpl<SysOrganMapper, SysOrgan> implements SysOrganService {

    private final SysDepartmentService sysDepartmentService;

    @Override
    public List<Tree<Integer>> selectTree(Integer tenantId, String organCode) {
        List<SysOrgan> list = this.list(Wrappers.<SysOrgan>lambdaQuery().eq(tenantId != null && tenantId > 0, SysOrgan::getTenantId, tenantId));
        List<TreeNode<Integer>> collect = list.stream().filter(sysOrgan -> sysOrgan.getId().intValue() != sysOrgan.getParentId())
        		.sorted(Comparator.comparingInt(SysOrgan::getSort))
        		.map(sysOrgan -> {
            TreeNode<Integer> treeNode = new TreeNode();
            treeNode.setId(sysOrgan.getId());
            treeNode.setParentId(sysOrgan.getParentId());
            treeNode.setName(sysOrgan.getOrganName());
            treeNode.setWeight(sysOrgan.getSort());
            // 扩展属性
            Map<String, Object> extra = new HashMap<>();
            extra.put("tenantId", sysOrgan.getTenantId());
            extra.put("organCode", sysOrgan.getOrganCode());
            extra.put("organName", sysOrgan.getOrganName());
            extra.put("sort", sysOrgan.getSort());
            extra.put("organType", sysOrgan.getOrganType());
            extra.put("economicTypeName", sysOrgan.getEconomicTypeName());
            extra.put("manageClassName", sysOrgan.getManageClassName());
            extra.put("address", sysOrgan.getAddress());
            extra.put("defaultPassword", sysOrgan.getDefaultPassword());
            treeNode.setExtra(extra);
            return treeNode;
        }).collect(Collectors.toList());
        List<Tree<Integer>> build = TreeUtil.build(collect, 0);
        if (!SecurityUtils.getRoles().contains(BuiltInRoleEnum.ADMIN.getId()) && StrUtil.isNotBlank(organCode)) {
            //不是超级管理员则根据一级机构编码返回机构树
            build = build.stream().filter(b -> b.get("organCode").equals(organCode.toString())).collect(Collectors.toList());
        }

        return build;
    }
    
    LambdaQueryWrapper<SysOrgan> getWrapper(SysOrgan organ) {
        // 构建查询条件
        LambdaQueryWrapper<SysOrgan> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(organ.getId() != null, SysOrgan::getId, organ.getId())
               .eq(organ.getTenantId() != null, SysOrgan::getTenantId, organ.getTenantId())
               .eq(organ.getParentId() != null, SysOrgan::getParentId, organ.getParentId())
               .like(organ.getOrganCode() != null, SysOrgan::getOrganCode, organ.getOrganCode())
               .like(organ.getOrganName() != null, SysOrgan::getOrganName, organ.getOrganName())
               .like(organ.getRegisterNo() != null, SysOrgan::getRegisterNo, organ.getRegisterNo())
               .orderByDesc(SysOrgan::getId)
               .last(CommonConstants.LIMIT_LIST_QUERY);  // 限制最大数量
        return wrapper;
    }
    
    @Override
    public R saveSysOrgan(SysOrgan sysOrgan) {
        R r = sysOrganCheck(sysOrgan, "0");
        if (r.getCode() != 0) return r;
        return R.ok(this.save(sysOrgan));
    }

    @Override
    public R updateSysOrganById(SysOrgan sysOrgan) {
        R r = sysOrganCheck(sysOrgan, "1");
        if (r.getCode() != 0) return r;
        return R.ok(this.updateById(sysOrgan));
    }

    @Override
    public R deleteSysOrgan(Integer id) {
        SysOrgan sysOrgan = this.getById(id);
        R r = sysOrganCheck(sysOrgan, "2");
        if (r.getCode() != 0) return r;
        return R.ok(this.removeById(id));
    }

    @Override
    public List<SysOrgan> getOrganListByUser(Integer userId) {
        return baseMapper.getOrganListByUser(userId);
    }


    /**
     * 机构新增修改参数校验
     * @param: type 0-新增 1-修改 2-删除
     */
    private R sysOrganCheck(SysOrgan sysOrgan, String type) {
        Long count = 0L;
        Long count2 = 0L;
        Long count3 = 0L;
        Long count4 = 0L;
        if (type.equals("0")) {
            //新增机构
            count = baseMapper.selectCount(Wrappers.<SysOrgan>lambdaQuery().eq(SysOrgan::getOrganName, sysOrgan.getOrganName()));
            count2 = baseMapper.selectCount(Wrappers.<SysOrgan>lambdaQuery().eq(SysOrgan::getOrganCode, sysOrgan.getOrganCode()));
        } else if (type.equals("1")) {
            //修改机构
            count = baseMapper.selectCount(Wrappers.<SysOrgan>lambdaQuery().eq(SysOrgan::getOrganName, sysOrgan.getOrganName()).ne(SysOrgan::getId, sysOrgan.getId()));
            count2 = baseMapper.selectCount(Wrappers.<SysOrgan>lambdaQuery().eq(SysOrgan::getOrganCode, sysOrgan.getOrganCode()).ne(SysOrgan::getId, sysOrgan.getId()));
        } else {
            //删除机构,校验是否有下级机构
            count3 = baseMapper.selectCount(Wrappers.<SysOrgan>lambdaQuery().eq(SysOrgan::getParentId, sysOrgan.getId()));
            //校验是否有下级科室
            count4 = sysDepartmentService.count(Wrappers.<SysDepartment>lambdaQuery().eq(SysDepartment::getOrganCode, sysOrgan.getOrganCode()));
        }
        if (count > 0) {
            return R.failed("机构名称不能重复");
        }
        if (count2 > 0) {
            return R.failed("机构编码不能重复");
        }
        if (count3 > 0) {
            return R.failed("该机构下存在下级机构，不允许删除");
        }
        if (count4 > 0) {
            return R.failed("该机构下存在下级科室，不允许删除");
        }
        return R.ok();
    }

}