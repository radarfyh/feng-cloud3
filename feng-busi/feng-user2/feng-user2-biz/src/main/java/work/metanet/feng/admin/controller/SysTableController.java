package work.metanet.feng.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import work.metanet.feng.admin.api.entity.SysTable;
import work.metanet.feng.admin.api.vo.TableFieldVO;
import work.metanet.feng.admin.service.SysTableService;
import work.metanet.feng.common.core.util.R;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;

import javax.annotation.Resource;
import java.util.List;

/**
 * 数据表(SysTable)表控制层
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("table")
@Tag(name = "数据表模块")
public class SysTableController {
    /**
     * 服务对象
     */
    @Resource
    private SysTableService tableService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param SysTable 查询实体
     * @return 所有数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询所有数据")
    @PreAuthorize("@pms.hasPermission('usr:table:query')")
    public R selectAll(Page page, SysTable SysTable) {
        return R.ok(this.tableService.page(page, new QueryWrapper<>()));
    }


    /**
     * 查询全部
     *
     * @return Response对象
     */
    @GetMapping("/list")
    @Operation(summary = "查询全部")
    @PreAuthorize("@pms.hasPermission('usr:table:query')")
    public R<List<SysTable>> list() {
           return R.ok( this.tableService.list());
    }

    @GetMapping("/getTableByDatabaseID/{databaseId}")
    @Operation(summary = "查询数据库下所有表")
    @PreAuthorize("@pms.hasPermission('usr:table:query')")
    public R<List<SysTable>> getTableByDatabaseID(@PathVariable String databaseId) {
        return R.ok( this.tableService.list((new LambdaQueryWrapper<SysTable>()).eq(SysTable::getDatasourceId,databaseId)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/id")
    @Operation(summary = "通过主键查询单条数据")
    public R<SysTable> selectOne(@RequestParam("id") String id) {
        return R.ok(this.tableService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param SysTable 实体对象
     * @return 新增结果
     */
    @PostMapping
    @Operation(summary = "新增数据")
    @PreAuthorize("@pms.hasPermission('usr:table:add')")
    public R insert(@RequestBody SysTable SysTable) {
        return R.ok(this.tableService.save(SysTable));
    }

    /**
     * 修改数据
     *
     * @param SysTable 实体对象
     * @return 修改结果
     */
    @PutMapping
    @Operation(summary = "修改数据")
    @PreAuthorize("@pms.hasPermission('usr:table:update')")
    public R update(@RequestBody SysTable SysTable) {
        return R.ok(this.tableService.updateById(SysTable));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    @Operation(summary = "删除数据")
    @PreAuthorize("@pms.hasPermission('usr:table:delete')")
    public R delete(@RequestParam("idList") List<String> idList) {
        return R.ok(this.tableService.removeByIds(idList));
    }
    
    @GetMapping("/getTableFields/{datasourceId}/{tableName}")
    @Operation(summary = "获取指定数据源下指定表名下的所有字段信息")
    public R<List<TableFieldVO>> getTableFields(@PathVariable String datasourceId, @PathVariable String tableName){
        R r = new R();
        try {
            r.setData(tableService.getTableFields(datasourceId,tableName));
        } catch (Exception e) {
            r.setCode(1);r.setMsg(e.getMessage());
            log.error(e.getMessage(),e);
        }
        return r;
    }
    
    @GetMapping("/getTableByDatasourceId/{datasourceId}")
    @Operation(summary = "获取指定数据源下的表")
    @PreAuthorize("@pms.hasPermission('usr:table:query')")
    public R<List<SysTable>> getTableByDatasourceId(@PathVariable String datasourceId){
        return R.ok( tableService.list(new LambdaQueryWrapper<SysTable>().eq(SysTable::getDatasourceId,datasourceId)));
    }
    
    @PostMapping("/ListById")
    @Operation(summary = "获取指定ID的表")
    public R<List<SysTable>> ListById(@RequestParam("idList")  List<String> idList){
        return  R.ok(this.tableService.listByIds(idList));
    }
}
