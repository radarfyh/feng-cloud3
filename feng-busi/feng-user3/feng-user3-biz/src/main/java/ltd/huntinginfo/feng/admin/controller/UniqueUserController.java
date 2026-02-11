package ltd.huntinginfo.feng.admin.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import ltd.huntinginfo.feng.admin.api.dto.dict.UniqueUserInfoDTO;
import ltd.huntinginfo.feng.admin.api.entity.dict.UniqueUser;
import ltd.huntinginfo.feng.admin.api.vo.dict.UniqueUserInfoVO;
import ltd.huntinginfo.feng.admin.service.dict.UniqueUserService;
import ltd.huntinginfo.feng.common.core.util.R;
import ltd.huntinginfo.feng.common.log.annotation.SysLog;
import ltd.huntinginfo.feng.common.security.annotation.HasPermission;

@RestController
@AllArgsConstructor
@RequestMapping("/dict/unique-user")
@Tag(name = "统一用户（员工）管理")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class UniqueUserController {

    private final UniqueUserService uniqueUserService;

    /**
     * 分页查询员工
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询员工")
    public R<IPage<UniqueUserInfoVO>> page(
            Page<UniqueUser> page,
            UniqueUserInfoDTO dto) {
        return R.ok(uniqueUserService.page(page, dto));
    }

    /**
     * 查询员工详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "员工详情")
    public R<UniqueUserInfoVO> getById(@PathVariable String id) {
        return R.ok(uniqueUserService.getUserById(id));
    }

    /**
     * 新增员工
     */
    @SysLog("新增员工")
    @PostMapping
    @HasPermission("unique_user_add")
    public R<Boolean> save(@RequestBody @Valid UniqueUserInfoDTO dto) {
        return R.ok(uniqueUserService.save(dto));
    }

    /**
     * 修改员工
     */
    @SysLog("修改员工")
    @PutMapping
    @HasPermission("unique_user_edit")
    public R<Boolean> update(@RequestBody @Valid UniqueUserInfoDTO dto) {
        return R.ok(uniqueUserService.updateById(dto));
    }

    /**
     * 删除员工
     */
    @SysLog("删除员工")
    @DeleteMapping("/{id}")
    @HasPermission("unique_user_del")
    public R<Boolean> delete(@PathVariable String id) {
        return R.ok(uniqueUserService.removeById(id));
    }

    /**
     * ⭐ 为员工开通系统登录账号
     */
    @SysLog("开通员工登录账号")
    @PostMapping("/{id}/enable-login")
    @HasPermission("unique_user_enable_login")
    @Operation(summary = "开通登录账号")
    public R<Boolean> enableLogin(@PathVariable String id) {
        return R.ok(uniqueUserService.enableLoginUser(id));
    }
}
