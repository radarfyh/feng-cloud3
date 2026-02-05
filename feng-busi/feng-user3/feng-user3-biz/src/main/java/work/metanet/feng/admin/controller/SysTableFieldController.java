package work.metanet.feng.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;
import work.metanet.feng.admin.api.dto.TableFileDTO;
import work.metanet.feng.admin.api.entity.SysTableField;
import work.metanet.feng.admin.api.vo.DataElementIdentifierInfoVO;
import work.metanet.feng.admin.api.vo.TableFieldVO;
import work.metanet.feng.admin.service.SysDatasourceService;
import work.metanet.feng.admin.service.SysTableFieldService;
import work.metanet.feng.common.core.util.R;

import java.util.List;

/**
 * 数据字段(SysTableField)表控制层
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@AllArgsConstructor
@RequestMapping("tableField")
@Tag(name = "数据字段模块")
public class SysTableFieldController {
    /**
     * 服务对象
     */
    @Autowired
    private SysTableFieldService tableFieldService;
    @Autowired
    private SysDatasourceService datasourceService;
    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param SysTableField 查询实体
     * @return 所有数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询所有数据")
    @PreAuthorize("@pms.hasPermission('usr:field:query')")
    public R selectAll(Page page, SysTableField SysTableField) {
        return R.ok(this.tableFieldService.page(page, new QueryWrapper<>()));
    }


    /**
     * 查询全部
     *
     * @return Response对象
     */
    @GetMapping("/list")
    @Operation(summary = "查询全部")
    @PreAuthorize("@pms.hasPermission('usr:field:query')")
    public R<List<SysTableField>> list() {
           return R.ok( this.tableFieldService.list());
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/id")
    @Operation(summary = "通过主键查询单条数据")
    public R<SysTableField> selectOne(@RequestParam("id") String id) {
        return R.ok(this.tableFieldService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param SysTableField 实体对象
     * @return 新增结果
     */
    @PostMapping
    @Operation(summary = "新增数据")
    @PreAuthorize("@pms.hasPermission('usr:field:add')")
    public R insert(@RequestBody SysTableField SysTableField) {
        return R.ok(this.tableFieldService.save(SysTableField));
    }

    /**
     * 修改数据
     *
     * @param SysTableField 实体对象
     * @return 修改结果
     */
    @PutMapping
    @Operation(summary = "修改数据")
    @PreAuthorize("@pms.hasPermission('usr:field:update')")
    public R update(@RequestBody SysTableField SysTableField) {
        return R.ok(this.tableFieldService.updateById(SysTableField));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    @Operation(summary = "删除数据")
    @PreAuthorize("@pms.hasPermission('usr:field:delete')")
    public R delete(@RequestParam("idList") List<String> idList) {
        return R.ok(this.tableFieldService.removeByIds(idList));
    }
    
    @PostMapping("/batchAdd")
    @Operation(summary = "批量保存")
    @PreAuthorize("@pms.hasPermission('usr:field:add')")
    public R batchAdd(@RequestBody TableFileDTO tableFileRequestDTO){
        return R.ok(tableFieldService.batchSave(tableFileRequestDTO));
    }

    @PostMapping("/batchUpdate")
    @Operation(summary = "字段信息批量更")
    @PreAuthorize("@pms.hasPermission('usr:field:update')")
    public R batchUpdate(@RequestBody TableFileDTO tableFileRequestDTO){
        return R.ok(tableFieldService.batchUpdate(tableFileRequestDTO));
    }
    
    @GetMapping ("/getTableFieldByTableId/{tableId}")
    @Operation(summary = "查询指定表下字段信息")
    public R<List<SysTableField>> getTableFieldByTableId(@PathVariable String tableId){
        return R.ok(tableFieldService.list((new LambdaQueryWrapper<SysTableField>()).eq(SysTableField::getTableId,tableId).orderByAsc(SysTableField::getFieldSequence)));
    }

    @GetMapping("/getAccurateQueryItems/{datasourceId}/{fieldNameChinese}")
    @Operation(summary = "获取精确查询条件项目")
    @Parameters({
    	@Parameter(name = "fieldNameChinese", description = "字段中文名"),
        @Parameter(name = "datasourceId", description = "数据库Id")
    })
    public R<List<TableFieldVO>> getAccurateQueryItems(@PathVariable String datasourceId,@PathVariable String fieldNameChinese ){
        return R.ok(tableFieldService.getAccurateQueryItems(fieldNameChinese, datasourceId));
    }

    @PostMapping("/ListById")
    @Operation(summary = "获取指定ID的字段")
    public R<List<SysTableField>> ListById(@RequestParam("idList")  List<String> idList){
        return  R.ok(this.tableFieldService.listByIds(idList));
    }
    
    @Operation(summary = "获取指定元数据标识的元素标识关联信息")
    @PostMapping("getIdentifierInfoByIdentifier")
    public R<List<DataElementIdentifierInfoVO>> getIdentifierInfoByIdentifier(@RequestParam String identifier){
        return R.ok(this.tableFieldService.getDataElementIdentifierInfo(null,null,null,null,identifier));
    }
    
    @Operation(summary = "获取指定字段Id的元素标识关联信息")
    @PostMapping("getIdentifierInfoByFieldId")
    public R<List<DataElementIdentifierInfoVO>> getIdentifierInfoFieldId(@RequestParam String fieldId){
        return R.ok(this.tableFieldService.getDataElementIdentifierInfo(null,null,null,fieldId,null));
    }

    @Operation(summary = "获取指定字段Id的元素标识关联信息")
    @PostMapping("getIdentifierInfoByJoint")
    public R<List<DataElementIdentifierInfoVO>> getIdentifierInfoByJoint(@RequestParam String datasourceId,@RequestParam String tableName,@RequestParam String fieldName){
        return R.ok(this.tableFieldService.getDataElementIdentifierInfo(datasourceId, tableName,fieldName,null,null));
    }
}
