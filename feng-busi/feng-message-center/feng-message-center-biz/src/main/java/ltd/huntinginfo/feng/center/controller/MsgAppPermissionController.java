package ltd.huntinginfo.feng.center.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ltd.huntinginfo.feng.common.core.controller.BaseController;
import ltd.huntinginfo.feng.common.core.util.R;
import ltd.huntinginfo.feng.center.api.dto.MsgAppPermissionDTO;
import ltd.huntinginfo.feng.center.api.vo.MsgAppPermissionVO;
import ltd.huntinginfo.feng.center.service.MsgAppPermissionService;

import java.util.List;

@RestController
@RequestMapping("/msgAppPermission")
@Tag(name = "（子）应用权限管理")
public class MsgAppPermissionController extends BaseController {

    @Autowired
    private MsgAppPermissionService msgAppPermissionService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询应用权限详情")
    @ApiResponse(responseCode = "200", description = "单条查询结果",
        content = @Content(schema = @Schema(implementation = MsgAppPermissionSingleResponse.class)))
    public R<MsgAppPermissionVO> getById(@PathVariable String id) {
        return R.ok(msgAppPermissionService.getById(id));
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询应用权限列表")
    @ApiResponse(responseCode = "200", description = "分页查询结果",
        content = @Content(schema = @Schema(implementation = MsgAppPermissionPageResponse.class)))
    public R<IPage<MsgAppPermissionVO>> page(
            @Parameter(description = "分页参数") Page page,
            @RequestBody MsgAppPermissionDTO msgAppPermission) {
        return R.ok(
            msgAppPermissionService.page(
                page, 
                msgAppPermission
            )
        );
    }

    @PostMapping("/list")
    @Operation(summary = "查询应用权限列表")
    @ApiResponse(responseCode = "200", description = "列表查询结果",
        content = @Content(schema = @Schema(implementation = MsgAppPermissionListResponse.class)))
    public R<List<MsgAppPermissionVO>> list(@RequestBody MsgAppPermissionDTO msgAppPermission) {
        return R.ok(msgAppPermissionService.list(msgAppPermission));
    }
    
    @PostMapping("/listActivePermissions")
    @Operation(summary = "查询活动的应用权限列表")
    @ApiResponse(responseCode = "200", description = "列表查询结果",
        content = @Content(schema = @Schema(implementation = MsgAppPermissionListResponse.class)))
    public R<List<MsgAppPermissionVO>> listActivePermissions(@PathVariable String appKey) {
        return R.ok(msgAppPermissionService.listActivePermissions(appKey));
    }

    @PostMapping
    @Operation(summary = "新增应用权限信息")
    public R<Boolean> save(@RequestBody MsgAppPermissionDTO msgAppPermission) {
        return R.ok(msgAppPermissionService.save(msgAppPermission));
    }

    @PutMapping
    @Operation(summary = "更新应用权限信息")
    public R<Boolean> update(@RequestBody MsgAppPermissionDTO msgAppPermission) {
        return R.ok(msgAppPermissionService.updateById(msgAppPermission));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除应用权限信息")
    public R<Boolean> remove(@PathVariable String id) {
        return R.ok(msgAppPermissionService.removeById(id));
    }
    
    // 定义用于Swagger文档的静态内部类，解析IPage对象
    private class MsgAppPermissionPageResponse extends R<IPage<MsgAppPermissionVO>> {
        private static final long serialVersionUID = 1L;
    }
    
    // 定义用于Swagger文档的静态内部类，解析List对象
    private class MsgAppPermissionListResponse extends R<List<MsgAppPermissionVO>> {
        private static final long serialVersionUID = 1L;
    }
    
    // 定义用于Swagger文档的静态内部类，解析单个对象
    private class MsgAppPermissionSingleResponse extends R<MsgAppPermissionVO> {
        private static final long serialVersionUID = 1L;
    }
}


