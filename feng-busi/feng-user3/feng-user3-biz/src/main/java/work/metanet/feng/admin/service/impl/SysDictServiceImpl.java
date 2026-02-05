package work.metanet.feng.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import work.metanet.feng.admin.api.entity.SysDictItem;
import work.metanet.feng.admin.mapper.SysDictItemMapper;
import work.metanet.feng.common.core.constant.CacheConstants;
import work.metanet.feng.common.core.constant.enums.DictTypeEnum;
import work.metanet.feng.common.core.util.R;
import lombok.AllArgsConstructor;
import work.metanet.feng.admin.mapper.SysDictMapper;
import work.metanet.feng.admin.api.entity.SysDict;
import work.metanet.feng.admin.service.SysDictService;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 字典表(SysDict)表服务实现类
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Service
@AllArgsConstructor
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements SysDictService {

    private final SysDictItemMapper dictItemMapper;

    /**
     * 根据ID 删除字典
     * @param id 字典ID
     * @return
     */
    @Override
    @CacheEvict(value = CacheConstants.DICT_DETAILS, allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public R removeDict(Integer id) {
        SysDict dict = this.getById(id);
        // 系统内置
        if (DictTypeEnum.SYSTEM.getType().equals(dict.getIsSystem())) {
            return R.failed("系统内置字典不能删除");
        }

        baseMapper.deleteById(id);
        dictItemMapper.delete(Wrappers.<SysDictItem>lambdaQuery().eq(SysDictItem::getDictId, id));
        return R.ok();
    }

    /**
     * 更新字典
     * @param dict 字典
     * @return
     */
    @Override
    @CacheEvict(value = CacheConstants.DICT_DETAILS, allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public R updateDict(SysDict dict) {
        SysDict sysDict = this.getById(dict.getId());
        // 系统内置
        if (DictTypeEnum.SYSTEM.getType().equals(sysDict.getIsSystem())) {
            return R.failed("系统内置字典不能修改");
        }
        return R.ok(this.updateById(dict));
    }
    
    /**
     * 通过字典类型查找字典项
     *
     * @param type 类型
     * @return 同类型字典
     */
    @Override
    @Cacheable(value = CacheConstants.DICT_DETAILS, key = "#type", unless = "#result.data.isEmpty()")
    public R<List<SysDictItem>> getDictByType(String type) {
        return R.ok(dictItemMapper.selectList(Wrappers.<SysDictItem>query().lambda().eq(StringUtils.isNotBlank(type), SysDictItem::getDictKey, type)));
    }
}