package work.metanet.feng.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import work.metanet.feng.admin.api.entity.SysDictItem;
import work.metanet.feng.common.core.util.R;

/**
 * 字典项(SysDictItem)表服务接口
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
public interface SysDictItemService extends IService<SysDictItem> {

    /*
     *
     * @Description: 修改字典项
     * @author edison
     * @date 2022/5/18
     * @param: sysDictItem
     * @return
     */
    R updateDictItem(SysDictItem sysDictItem);

    /*
     *
     * @Description: 通过id删除字典项
     * @author edison
     * @date 2022/5/18
     * @param: id
     * @return
     */
    R removeDictItem(Integer id);
}