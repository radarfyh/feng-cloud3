package ltd.huntinginfo.feng.ai.service.impl;

import cn.hutool.core.util.StrUtil;
import ltd.huntinginfo.feng.ai.api.entity.AigcModel;
import ltd.huntinginfo.feng.ai.mapper.AigcModelMapper;
import ltd.huntinginfo.feng.ai.service.AigcModelService;
import ltd.huntinginfo.feng.common.core.constant.enums.ModelTypeEnum;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 模型服务实现类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Service
@RequiredArgsConstructor
public class AigcModelServiceImpl extends ServiceImpl<AigcModelMapper, AigcModel> implements AigcModelService {

    @Override
    public List<AigcModel> getChatModels() {
        List<AigcModel> list = baseMapper.selectList(Wrappers.<AigcModel>lambdaQuery()
                .eq(AigcModel::getType, ModelTypeEnum.CHAT.getCode()));
        list.forEach(this::hide);
        return list;
    }

    @Override
    public List<AigcModel> getImageModels() {
        List<AigcModel> list = baseMapper.selectList(Wrappers.<AigcModel>lambdaQuery()
                .eq(AigcModel::getType, ModelTypeEnum.IMAGE.getCode()));
        list.forEach(this::hide);
        return list;
    }

    @Override
    public List<AigcModel> getEmbeddingModels() {
        List<AigcModel> list = baseMapper.selectList(Wrappers.<AigcModel>lambdaQuery()
                .eq(AigcModel::getType, ModelTypeEnum.EMBEDDING.getCode()));
        list.forEach(this::hide);
        return list;
    }

    @Override
    public List<AigcModel> list(AigcModel data) {
        List<AigcModel> list = this.list(Wrappers.<AigcModel>lambdaQuery()
	                .eq(data.getType() != null && StrUtil.isNotBlank(data.getType()), AigcModel::getType, data.getType())
	                .eq(data.getStatus() != null, AigcModel::getStatus, data.getStatus())
	                .eq(data.getProvider() != null && StrUtil.isNotBlank(data.getProvider()), AigcModel::getProvider, data.getProvider())
	        		.like(StrUtil.isNotBlank(data.getName()), AigcModel::getName, data.getName())
	        		.like(StrUtil.isNotBlank(data.getModel()), AigcModel::getModel, data.getModel())
        		);
        list.forEach(this::hide);
        return list;
    }

    @Override
    public Page<AigcModel> page(AigcModel data, Page<AigcModel> page) {
        
        Page<AigcModel> iPage = this.page(page, Wrappers.<AigcModel>lambdaQuery()
        		.like(StrUtil.isNotBlank(data.getName()), AigcModel::getName, data.getName())
        		.eq(data.getType() != null, AigcModel::getType, data.getType())
        		.like(data.getProvider() != null && StrUtil.isNotBlank(data.getProvider()), AigcModel::getProvider, data.getProvider())
        		.orderByDesc(AigcModel::getCreateTime));

        iPage.getRecords().forEach(this::hide);
        return iPage;
    }

    @Override
    public AigcModel selectById(Integer id) {
        AigcModel model = this.getById(id);
        hide(model);
        return model;
    }

    private void hide(AigcModel model) {
        if (model == null || StrUtil.isBlank(model.getApiKey())) {
            return;
        }
        String key = StrUtil.hide(model.getApiKey(), 3, model.getApiKey().length() - 4);
        model.setApiKey(key);

        if (StrUtil.isBlank(model.getSecretKey())) {
            return;
        }
        String sec = StrUtil.hide(model.getSecretKey(), 3, model.getSecretKey().length() - 4);
        model.setSecretKey(sec);
    }
}

