package work.metanet.feng.admin.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.metanet.feng.admin.api.entity.SysAffiliation;
import work.metanet.feng.admin.api.entity.SysAffiliationOrgan;
import work.metanet.feng.admin.api.entity.SysOrgan;
import work.metanet.feng.admin.mapper.SysAffiliationMapper;
import work.metanet.feng.admin.service.SysAffiliationOrganService;
import work.metanet.feng.admin.service.SysAffiliationService;
import work.metanet.feng.admin.service.SysOrganService;
import work.metanet.feng.common.core.util.R;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 联盟信息表(SysAffiliation)表服务实现类
 *
 * @author edison
 * @since 2023-08-02 09:50:08
 */
@Service
@AllArgsConstructor
public class SysAffiliationServiceImpl extends ServiceImpl<SysAffiliationMapper, SysAffiliation> implements SysAffiliationService {

    private final SysOrganService sysOrganService;

    private final SysAffiliationOrganService sysAffiliationOrganService;

    @Override
    public R<List<SysAffiliation>> getOrganCodeBySysAffiliationList(String organCode) {
        List<SysAffiliation> sysAffiliationList = null;
        SysOrgan sysOrgan = sysOrganService.getOne(Wrappers.<SysOrgan>lambdaQuery().eq(SysOrgan::getOrganCode, organCode));
        if (Objects.isNull(sysOrgan)) {
            return R.failed("该机构不存在");
        }
        List<SysAffiliationOrgan> sysAffiliationOrganList = sysAffiliationOrganService.list(Wrappers.<SysAffiliationOrgan>lambdaQuery().eq(SysAffiliationOrgan::getOrganId, sysOrgan.getId()));
        if (CollectionUtil.isNotEmpty(sysAffiliationOrganList)) {
            List<Integer> affiliationIds = sysAffiliationOrganList.stream().map(SysAffiliationOrgan::getAffiliationId).collect(Collectors.toList());
            sysAffiliationList = listByIds(affiliationIds);
        }
        return R.ok(sysAffiliationList);
    }
}
