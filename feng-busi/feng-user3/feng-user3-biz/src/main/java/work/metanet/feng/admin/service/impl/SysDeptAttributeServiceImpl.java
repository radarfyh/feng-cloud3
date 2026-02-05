package work.metanet.feng.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import work.metanet.feng.admin.mapper.SysDeptAttributeMapper;
import work.metanet.feng.admin.api.entity.SysDeptAttribute;
import work.metanet.feng.admin.service.SysDeptAttributeService;
import org.springframework.stereotype.Service;

/**
 * 科室属性关联表(SysDeptAttribute)表服务实现类
 *
 * @author edison
 * @since 2022-11-01 11:39:30
 */
@Service
@AllArgsConstructor
public class SysDeptAttributeServiceImpl extends ServiceImpl<SysDeptAttributeMapper, SysDeptAttribute> implements SysDeptAttributeService {

}
