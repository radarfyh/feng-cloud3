package work.metanet.feng.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import work.metanet.feng.admin.mapper.SysTableExtAttrMapper;
import work.metanet.feng.admin.api.entity.SysTableExtAttr;
import work.metanet.feng.admin.service.SysTableExtAttrService;
import org.springframework.stereotype.Service;

/**
 * 用户扩展属性配置(SysTableExtAttr)表服务实现类
 *
 * @author edison
 * @since 2022-05-11 16:04:07
 */
@Service
@AllArgsConstructor
public class SysTableExtAttrServiceImpl extends ServiceImpl<SysTableExtAttrMapper, SysTableExtAttr> implements SysTableExtAttrService {

}