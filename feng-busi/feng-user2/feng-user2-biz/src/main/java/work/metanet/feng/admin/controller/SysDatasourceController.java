package work.metanet.feng.admin.controller;


import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import work.metanet.feng.admin.api.dto.DatasourceSqlDTO;
import work.metanet.feng.admin.api.dto.SysDatasourceDTO;
import work.metanet.feng.admin.api.entity.SysDatasource;
import work.metanet.feng.admin.api.vo.TableVO;
import work.metanet.feng.admin.service.SysDatasourceService;
import work.metanet.feng.common.core.constant.CommonConstants;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.log.annotation.SysLog;
import work.metanet.feng.common.security.annotation.Inner;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * 数据源表(SysDatasource)表控制层
 *
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@AllArgsConstructor
@RequestMapping("/sysDatasource")
@Tag(name = "数据源模块")
public class SysDatasourceController {
    /**
     * 服务对象
     */
    private final SysDatasourceService sysDatasourceService;

    /**
     * 分页查询所有数据
     *
     * @param page          分页对象
     * @param sysDatasource 查询实体
     * @return 所有数据
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询所有数据")
    public R selectAll(Page page, SysDatasource sysDatasource) {
        return R.ok(this.sysDatasourceService.page(page, Wrappers.<SysDatasource>lambdaQuery()
        		.like(StringUtils.isNotBlank(sysDatasource.getName()), SysDatasource::getName, sysDatasource.getName())
        		.like(StringUtils.isNotBlank(sysDatasource.getDbName()), SysDatasource::getDbName, sysDatasource.getDbName())
        		.eq(StringUtils.isNotBlank(sysDatasource.getOrganCode()), SysDatasource::getOrganCode, sysDatasource.getOrganCode())
        		.eq(StringUtils.isNotBlank(sysDatasource.getApplicationCode()), SysDatasource::getApplicationCode, sysDatasource.getApplicationCode())
        		.eq(StringUtils.isNotBlank(sysDatasource.getDbType()), SysDatasource::getDbType, sysDatasource.getDbType())
        		.orderByDesc(SysDatasource::getCreateTime)));
    }


    /**
     * 查询全部
     *
     * @return Response对象
     */
    @GetMapping("/list")
    @Operation(summary = "查询全部")
    public R<List<SysDatasource>> list(@RequestHeader(CommonConstants.ORGAN_CODE) String organCode) {
        return R.ok(this.sysDatasourceService.list(Wrappers.<SysDatasource>lambdaQuery().eq(SysDatasource::getOrganCode, organCode)));
    }

    /**
     * 通过主键查询单条数据，内部feign接口
     *
     * @param id 主键
     * @return 单条数据
     */
    @Inner
    @GetMapping("{id}")
    @Operation(summary = "通过主键查询单条数据,内部feign接口")
    public R<SysDatasource> selectOne(@PathVariable Integer id) {
        return R.ok(this.sysDatasourceService.getById(id));
    }

    /**
     * 新增数据源表
     *
     * @param sysDatasource 数据源表
     * @return R
     */
    @SysLog("新增数据源")
    @PostMapping
    @Operation(summary = "新增数据源")
    public R saveDsByEnc(@RequestBody SysDatasource sysDatasource) {
        return sysDatasourceService.saveDsByEnc(sysDatasource);
    }

    /**
     * 根据填写参数测试数据源连接
     *
     * @param sysDatasourceDTO
     * @return R
     */
    @PostMapping("/testDatasource")
    @Operation(summary = "根据填写参数测试数据源连接")
    public R testDatasource(@RequestBody SysDatasourceDTO sysDatasourceDTO) {
        return sysDatasourceService.testDatasource(sysDatasourceDTO);
    }

    /**
     * 根据id测试指定数据源连接
     *
     * @param id
     * @return R
     */
    @GetMapping("/testDatasourceById")
    @Operation(summary = "根据id测试指定数据源连接")
    public R testDatasourceById(@RequestParam("id") Integer id) {
        return sysDatasourceService.testDatasourceById(id);
    }


    /**
     * 动态执行SQL语句
     *
     * @param datasourceSqlDTO 数据源表
     * @return R
     */
    @PostMapping("/dynamicSql")
    @Operation(summary = "动态执行SQL语句")
    public R dynamicSql(@RequestBody DatasourceSqlDTO datasourceSqlDTO) {
        return sysDatasourceService.dynamicSql(datasourceSqlDTO);
    }

    /**
     * 修改数据
     *
     * @param sysDatasource 实体对象
     * @return 修改结果
     */
    @SysLog("修改数据源")
    @PutMapping
    @Operation(summary = "修改数据源")
    public R updateDsByEnc(@RequestBody SysDatasource sysDatasource) {
        return sysDatasourceService.updateDsByEnc(sysDatasource);
    }

    /**
     * 通过id删除数据源
     *
     * @param idList id集合
     * @return R
     */
    @SysLog("删除数据源")
    @DeleteMapping
    @Operation(summary = "通过id删除数据源")
    public R removeById(@RequestParam("idList") List<Integer> idList) {
        return R.ok(sysDatasourceService.removeBatchByIds(idList));
    }
    
    @Operation(summary = "数据源类型")
    @GetMapping("/types")
    public Collection types() throws Exception {
        return sysDatasourceService.types();
    }
    
    @Operation(summary = "查询数据源下属所有表")
    @GetMapping("/getTables/{id}")
    public R<List<TableVO>> getTables(@PathVariable String id) throws Exception {
        return R.ok(sysDatasourceService.getTables(id));
    }
}