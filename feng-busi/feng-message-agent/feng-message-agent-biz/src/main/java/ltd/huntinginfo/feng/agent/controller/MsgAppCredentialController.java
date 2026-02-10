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
import ltd.huntinginfo.feng.agent.api.dto.MsgAppCredentialDTO;
import ltd.huntinginfo.feng.agent.api.vo.MsgAppCredentialVO;
import ltd.huntinginfo.feng.agent.service.MsgAppCredentialService;

import java.util.List;

@RestController
@RequestMapping("/msgAppCredential")
@Tag(name = "（子）应用凭证管理")
public class MsgAppCredentialController extends BaseController {

    @Autowired
    private MsgAppCredentialService msgAppCredentialService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询应用凭证详情")
    @ApiResponse(responseCode = "200", description = "单条查询结果",
        content = @Content(schema = @Schema(implementation = MsgAppCredentialSingleResponse.class)))
    public R<MsgAppCredentialVO> getById(@PathVariable String id) {
        return R.ok(msgAppCredentialService.getById(id));
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询应用凭证列表")
    @ApiResponse(responseCode = "200", description = "分页查询结果",
        content = @Content(schema = @Schema(implementation = MsgAppCredentialPageResponse.class)))
    public R<IPage<MsgAppCredentialVO>> page(
            @Parameter(description = "分页参数") Page page,
            @RequestBody MsgAppCredentialDTO msgAppCredential) {
        return R.ok(
            msgAppCredentialService.page(
                page, 
                msgAppCredential
            )
        );
    }

    @PostMapping("/list")
    @Operation(summary = "查询应用凭证列表")
    @ApiResponse(responseCode = "200", description = "列表查询结果",
        content = @Content(schema = @Schema(implementation = MsgAppCredentialListResponse.class)))
    public R<List<MsgAppCredentialVO>> list(@RequestBody MsgAppCredentialDTO msgAppCredential) {
        return R.ok(msgAppCredentialService.list(msgAppCredential));
    }

    @PostMapping
    @Operation(summary = "新增应用凭证信息")
    public R<Boolean> save(@RequestBody MsgAppCredentialDTO msgAppCredential) {
        return R.ok(msgAppCredentialService.save(msgAppCredential));
    }

    @PutMapping
    @Operation(summary = "更新应用凭证信息")
    public R<Boolean> update(@RequestBody MsgAppCredentialDTO msgAppCredential) {
        return R.ok(msgAppCredentialService.updateById(msgAppCredential));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除应用凭证信息")
    public R<Boolean> remove(@PathVariable String id) {
        return R.ok(msgAppCredentialService.removeById(id));
    }
    
    // 定义用于Swagger文档的静态内部类，解析IPage对象
    private class MsgAppCredentialPageResponse extends R<IPage<MsgAppCredentialVO>> {
        private static final long serialVersionUID = 1L;
    }
    
    // 定义用于Swagger文档的静态内部类，解析List对象
    private class MsgAppCredentialListResponse extends R<List<MsgAppCredentialVO>> {
        private static final long serialVersionUID = 1L;
    }
    
    // 定义用于Swagger文档的静态内部类，解析单个对象
    private class MsgAppCredentialSingleResponse extends R<MsgAppCredentialVO> {
        private static final long serialVersionUID = 1L;
    }
}

