package ltd.huntinginfo.feng.admin.service.dict;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

public interface BaseDictService<T> extends IService<T> {
    /**
     * 根据代码获取字典项
     */
    T getByCode(String code);

    /**
     * 获取所有有效字典项
     */
    List<T> getAllValidItems();

    /**
     * 刷新缓存
     */
    void refreshCache();
}
