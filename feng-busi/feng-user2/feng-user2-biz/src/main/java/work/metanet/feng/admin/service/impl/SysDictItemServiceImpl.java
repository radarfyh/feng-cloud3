package work.metanet.feng.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import work.metanet.feng.admin.api.entity.SysDict;
import work.metanet.feng.admin.service.SysDictService;
import work.metanet.feng.common.core.constant.CacheConstants;
import work.metanet.feng.common.core.constant.enums.DictTypeEnum;
import work.metanet.feng.common.core.util.R;
import lombok.AllArgsConstructor;
import work.metanet.feng.admin.mapper.SysDictItemMapper;
import work.metanet.feng.admin.api.entity.SysDictItem;
import work.metanet.feng.admin.service.SysDictItemService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

/**
 * 字典项(SysDictItem)表服务实现类
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Service
@AllArgsConstructor
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, SysDictItem> implements SysDictItemService {

    private final SysDictService dictService;

    /**
     * 更新字典项
     *
     * @param item 字典项
     * @return
     */
    @Override
    @CacheEvict(value = CacheConstants.DICT_DETAILS, allEntries = true)
    public R updateDictItem(SysDictItem item) {
        // 查询字典
        SysDict dict = dictService.getById(item.getDictId());
        // 系统内置
        if (DictTypeEnum.SYSTEM.getType().equals(dict.getIsSystem())) {
            return R.failed("系统内置字典项目不能删除");
        }
        return R.ok(this.updateById(item));
    }

    /**
     * 删除字典项
     *
     * @param id 字典项ID
     * @return
     */
    @Override
    @CacheEvict(value = CacheConstants.DICT_DETAILS, allEntries = true)
    public R removeDictItem(Integer id) {
        // 根据ID查询字典ID
        SysDictItem dictItem = this.getById(id);
        SysDict dict = dictService.getById(dictItem.getDictId());
        // 系统内置
        if (DictTypeEnum.SYSTEM.getType().equals(dict.getIsSystem())) {
            return R.failed("系统内置字典项目不能删除");
        }
        return R.ok(this.removeById(id));
    }
}