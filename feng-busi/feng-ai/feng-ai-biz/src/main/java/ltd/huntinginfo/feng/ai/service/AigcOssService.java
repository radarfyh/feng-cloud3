package ltd.huntinginfo.feng.ai.service;

import com.baomidou.mybatisplus.extension.service.IService;

import ltd.huntinginfo.feng.ai.api.entity.AigcOss;

import org.springframework.web.multipart.MultipartFile;

/**
 * AI文件服务接口
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

public interface AigcOssService extends IService<AigcOss> {

    /**
     * 上传文件
     * 已废弃(改用feng-user3-biz.SysFileController.upload)
     */
	@Deprecated
    AigcOss upload(MultipartFile file, Integer userId);
    
    String extractFileContent(Integer id);

}

