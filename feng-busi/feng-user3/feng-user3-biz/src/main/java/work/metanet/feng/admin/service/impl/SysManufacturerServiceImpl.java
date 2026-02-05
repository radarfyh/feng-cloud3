package work.metanet.feng.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import work.metanet.feng.admin.mapper.SysManufacturerMapper;
import work.metanet.feng.admin.api.entity.SysManufacturer;
import work.metanet.feng.admin.service.SysManufacturerService;
import org.springframework.stereotype.Service;

/**
 * 厂商表(SysManufacturer)表服务实现类
 *
 * @author edison
 * @since 2022-06-09 15:03:37
 */
@Service
@AllArgsConstructor
public class SysManufacturerServiceImpl extends ServiceImpl<SysManufacturerMapper, SysManufacturer> implements SysManufacturerService {

}