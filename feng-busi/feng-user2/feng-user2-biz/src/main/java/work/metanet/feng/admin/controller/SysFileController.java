package work.metanet.feng.admin.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import work.metanet.feng.admin.api.entity.SysFile;
import work.metanet.feng.admin.service.SysFileService;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.log.annotation.SysLog;
import work.metanet.feng.common.security.annotation.Inner;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 文件管理表(SysFile)表控制层
 *
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RestController
@AllArgsConstructor
@RequestMapping("/sysFile")
@Tag(name = "文件管理模块")
public class SysFileController {
    @Autowired
    private SysFileService sysFileService;

    /**
     * 分页查询
     *
     * @param page    分页对象
     * @param sysFile 文件管理
     * @return
     */
    @Operation(summary = "分页查询", description = "分页查询")
    @GetMapping("/page")
    public R getSysFilePage(Page page, SysFile sysFile) {
        return R.ok(sysFileService.page(page, Wrappers.query(sysFile)));
    }

    /**
     * 通过id删除文件管理
     *
     * @param id id
     * @return R
     */
    @Operation(summary = "通过id删除文件管理", description = "通过id删除文件管理")
    @SysLog("删除文件管理")
    @DeleteMapping("/{id}")
    @PreAuthorize("@pms.hasPermission('file_del')")
    public R removeById(@PathVariable Long id) {
        return R.ok(sysFileService.deleteFile(id));
    }

    /**
     * 默认上传文件 文件名采用uuid,避免原始文件名中带"-"符号导致下载的时候解析出现异常
     *
     * @param file 资源
     * @return R(/ admin / bucketName / filename)
     */
    @Operation(summary = "默认上传文件", description = "默认feng-bucket桶下存储文件")
    @PostMapping("/upload")
    public R upload(@RequestParam("file") MultipartFile file) {
        return sysFileService.uploadFile(file);
    }

    /**
     * 原文件名上传
     *
     * @param file 资源
     * @return R(/ admin / bucketName / filename)
     */
    @Operation(summary = "原文件名上传", description = "默认feng-bucket桶下存储文件")
    @PostMapping("/originalUpload")
    public R originalUpload(@RequestParam("file") MultipartFile file) {
        return sysFileService.originalUpload(file);
    }

    /**
     * 自定义文件夹上传文件 文件名采用uuid,避免原始文件名中带"-"符号导致下载的时候解析出现异常
     *
     * @param file 资源
     * @param dir  文件存放目录
     * @return R(/ admin / bucketName / filename)
     */
    @Operation(summary = "自定义文件夹上传", description = "默认feng-bucket桶下自定义文件夹目录存储文件")
    @PostMapping("/upload-dir")
    public R upload(@RequestParam("file") MultipartFile file, @RequestParam("dir") String dir) throws IOException {
        return sysFileService.uploadFile(file, dir);
    }

    /**
     * 指定桶文件上传
     *
     * @param file 资源
     * @return R(/ admin / bucketName / filename)
     */
    @Operation(summary = "指定桶文件上传", description = "指定桶")
    @PostMapping("/bucketNameUpload")
    public R bucketNameUpload(@RequestParam("file") MultipartFile file,@RequestParam("bucketName") String bucketName) {
        return sysFileService.bucketNameUpload(file,bucketName);
    }

    /**
     * 获取文件
     *
     * @param bucket   桶名称
     * @param fileName 文件空间/名称
     * @param response
     * @return
     */
    @Inner(false)
    @Operation(summary = "获取文件", description = "无文件目录获取文件")
    @GetMapping("/{bucket}/{fileName}")
    public void file(@PathVariable String bucket, @PathVariable String fileName, HttpServletResponse response) {
        sysFileService.getFile(bucket, fileName, response);
    }

    /**
     * 获取文件
     *
     * @param bucket   桶名称
     * @param folder   文件夹
     * @param fileName 文件空间/名称
     * @param response
     * @return
     */
    @Inner(false)
    @Operation(summary = "获取文件", description = "带一级文件目录获取文件")
    @GetMapping("/{bucket}/{folder}/{fileName}")
    public void getFile(@PathVariable String bucket, @PathVariable String folder, @PathVariable String fileName, HttpServletResponse response) {
        sysFileService.getFile(bucket, "/" + folder + "/" + fileName, response);
    }
}