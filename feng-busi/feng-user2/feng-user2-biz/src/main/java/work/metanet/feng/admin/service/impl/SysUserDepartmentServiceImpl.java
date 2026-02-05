package work.metanet.feng.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import work.metanet.feng.admin.mapper.SysUserDepartmentMapper;
import work.metanet.feng.admin.api.entity.SysUserDepartment;
import work.metanet.feng.admin.service.SysUserDepartmentService;
import org.springframework.stereotype.Service;

/**
 * 用户科室表(SysUserDepartment)表服务实现类
 *
 * @author edison
 * @since 2022-05-11 16:04:07
 */
@Service
@AllArgsConstructor
public class SysUserDepartmentServiceImpl extends ServiceImpl<SysUserDepartmentMapper, SysUserDepartment> implements SysUserDepartmentService {

}