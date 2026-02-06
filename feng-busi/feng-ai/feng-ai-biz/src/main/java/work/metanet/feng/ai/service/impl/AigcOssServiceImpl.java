package work.metanet.feng.ai.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import work.metanet.feng.ai.api.entity.AigcOss;
import work.metanet.feng.ai.mapper.AigcOssMapper;
import work.metanet.feng.ai.service.AigcOssService;
import work.metanet.feng.ai.utils.FileParseUtil;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 文件资源管理 - 服务实现类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class AigcOssServiceImpl extends ServiceImpl<AigcOssMapper, AigcOss> implements AigcOssService {

    private final FileStorageService fileStorageService;

    /**
     * 上传文件
     * 已废弃(改用feng-user3-biz.SysFileController.upload)
     */
    @Override
    @Deprecated
    public AigcOss upload(MultipartFile file, Integer userId) {
        log.info(">>>>>>>>>>>>>> OSS文件上传开始： {}", file.getOriginalFilename());
        FileInfo info = fileStorageService.of(file)
                .setPath(DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN))
                .upload();
        log.info(">>>>>>>>>>>>>> OSS文件上传结束： {} - {}", info.getFilename(), info.getUrl());
        AigcOss oss = BeanUtil.copyProperties(info, AigcOss.class);
        oss.setFileId(info.getId());
        oss.setUserId(userId);
        this.save(oss);
        return oss;
    }

	@Override
	public String extractFileContent(Integer id) {
		AigcOss oss = this.getById(id);
		return FileParseUtil.extractFileContent(oss.getPath(), oss.getExt());
	}

}

