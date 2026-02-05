package work.metanet.feng.admin.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import work.metanet.feng.admin.api.entity.SysDict;
import work.metanet.feng.admin.api.entity.SysDictItem;
import work.metanet.feng.common.core.util.R;

/**
 * 字典表(SysDict)表服务接口
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
public interface SysDictService extends IService<SysDict> {

    /**
     * 删除字典
     * @param: id
     */
    R removeDict(Integer id);

    /**
     * 修改字典
     * @param: sysDict
     */
    R updateDict(SysDict sysDict);

	/**
	 * 通过字典类型查找字典项
	 *
	 * @param type 类型
	 * @return 同类型字典
	 */
    R<List<SysDictItem>> getDictByType(String type);
}