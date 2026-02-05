package work.metanet.feng.admin.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.metanet.feng.admin.api.dto.SysAffiliationOrganDTO;
import work.metanet.feng.admin.api.entity.SysOrgan;
import work.metanet.feng.common.core.util.R;
import lombok.AllArgsConstructor;
import work.metanet.feng.admin.mapper.SysAffiliationOrganMapper;
import work.metanet.feng.admin.api.entity.SysAffiliationOrgan;
import work.metanet.feng.admin.service.SysAffiliationOrganService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 联盟机构关联表(SysAffiliationOrgan)表服务实现类
 *
 * @author edison
 * @since 2023-08-02 09:53:02
 */
@Service
@AllArgsConstructor
public class SysAffiliationOrganServiceImpl extends ServiceImpl<SysAffiliationOrganMapper, SysAffiliationOrgan> implements SysAffiliationOrganService {

    @Override
    public List<SysOrgan> getOrganListByAffiliationId(Integer affiliationId) {
        return baseMapper.getOrganListByAffiliationId(affiliationId);
    }

    @Transactional
    @Override
    public R configSysAffiliationOrgan(SysAffiliationOrganDTO sysAffiliationOrganDTO) {
        //1、先删除当前联盟下的所有机构
        baseMapper.delete(Wrappers.<SysAffiliationOrgan>lambdaQuery().eq(SysAffiliationOrgan::getAffiliationId, sysAffiliationOrganDTO.getAffiliationId()));
        //2、再新增当前联盟下的所有机构
        List<SysAffiliationOrgan> list = new ArrayList<>();
        List<Integer> organIdList = sysAffiliationOrganDTO.getOrganIdList();
        if (CollectionUtil.isNotEmpty(organIdList)) {
            organIdList.forEach(organId -> {
                SysAffiliationOrgan sysAffiliationOrgan = new SysAffiliationOrgan();
                sysAffiliationOrgan.setAffiliationId(sysAffiliationOrgan.getAffiliationId());
                sysAffiliationOrgan.setOrganId(organId);
                list.add(sysAffiliationOrgan);
            });
        }
        return R.ok(saveBatch(list));
    }
}
