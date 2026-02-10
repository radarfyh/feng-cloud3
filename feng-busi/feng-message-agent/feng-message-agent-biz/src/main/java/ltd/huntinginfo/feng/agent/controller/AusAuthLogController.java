package ltd.huntinginfo.feng.agent.controller;

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
import ltd.huntinginfo.feng.agent.api.dto.AusAuthLogDTO;
import ltd.huntinginfo.feng.agent.api.vo.AusAuthLogVO;
import ltd.huntinginfo.feng.agent.service.AusAuthLogService;

import java.util.List;

@RestController
@RequestMapping("/ausAuthLog")
@Tag(name = "AUS认证日志")
public class AusAuthLogController extends BaseController {

    @Autowired
    private AusAuthLogService ausAuthLogService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询AUS认证日志详情")
    @ApiResponse(responseCode = "200", description = "单条查询结果",
        content = @Content(schema = @Schema(implementation = AusAuthLogSingleResponse.class)))
    public R<AusAuthLogVO> getById(@PathVariable Integer id) {
        return R.ok(ausAuthLogService.getById(id));
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询AUS认证日志列表")
    @ApiResponse(responseCode = "200", description = "分页查询结果",
        content = @Content(schema = @Schema(implementation = AusAuthLogPageResponse.class)))
    public R<IPage<AusAuthLogVO>> page(
            @Parameter(description = "分页参数") Page page,
            @RequestBody AusAuthLogDTO ausAuthLog) {
        return R.ok(
            ausAuthLogService.page(
                page, 
                ausAuthLog
            )
        );
    }

    @PostMapping("/list")
    @Operation(summary = "查询AUS认证日志列表")
    @ApiResponse(responseCode = "200", description = "列表查询结果",
        content = @Content(schema = @Schema(implementation = AusAuthLogListResponse.class)))
    public R<List<AusAuthLogVO>> list(@RequestBody AusAuthLogDTO ausAuthLog) {
        return R.ok(ausAuthLogService.list(ausAuthLog));
    }

    @PostMapping
    @Operation(summary = "新增AUS认证日志信息")
    public R<Boolean> save(@RequestBody AusAuthLogDTO ausAuthLog) {
        return R.ok(ausAuthLogService.save(ausAuthLog));
    }

    @PutMapping
    @Operation(summary = "更新AUS认证日志信息")
    public R<Boolean> update(@RequestBody AusAuthLogDTO ausAuthLog) {
        return R.ok(ausAuthLogService.updateById(ausAuthLog));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除AUS认证日志信息")
    public R<Boolean> remove(@PathVariable String id) {
        return R.ok(ausAuthLogService.removeById(id));
    }
    
    // 定义用于Swagger文档的静态内部类，解析IPage对象
    private class AusAuthLogPageResponse extends R<IPage<AusAuthLogVO>> {
        private static final long serialVersionUID = 1L;
    }
    
    // 定义用于Swagger文档的静态内部类，解析List对象
    private class AusAuthLogListResponse extends R<List<AusAuthLogVO>> {
        private static final long serialVersionUID = 1L;
    }
    
    // 定义用于Swagger文档的静态内部类，解析单个对象
    private class AusAuthLogSingleResponse extends R<AusAuthLogVO> {
        private static final long serialVersionUID = 1L;
    }
}

