package ltd.huntinginfo.feng.ai.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import ltd.huntinginfo.feng.ai.api.entity.AigcApp;
import ltd.huntinginfo.feng.ai.api.entity.AigcKnowledge;
import ltd.huntinginfo.feng.ai.api.entity.AigcModel;
import ltd.huntinginfo.feng.ai.mapper.AigcAppMapper;
import ltd.huntinginfo.feng.ai.mapper.AigcKnowledgeMapper;
import ltd.huntinginfo.feng.ai.mapper.AigcModelMapper;
import ltd.huntinginfo.feng.ai.service.AigcAppService;
import ltd.huntinginfo.feng.common.core.util.R;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI应用程序服务实现类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RequiredArgsConstructor
@Service
public class AigcAppServiceImpl extends ServiceImpl<AigcAppMapper, AigcApp> implements AigcAppService {

    private final AigcModelMapper aigcModelMapper;
    private final AigcKnowledgeMapper aigcKnowledgeMapper;

    @Override
    public List<AigcApp> list(AigcApp data) {
        List<AigcApp> list = baseMapper.selectList(getWrapper(data));
        
        List<AigcApp> ret = list.stream().map(this::convertToVO).collect(Collectors.toList());

        return ret;
    }

    @Override
    public IPage<AigcApp> page(Page<AigcApp> page, AigcApp data) {
    	IPage<AigcApp> ret = baseMapper.selectPage(page, getWrapper(data)).convert(this::convertToVO);
        return ret;
    }
    
    Wrapper<AigcApp> getWrapper(AigcApp data) {
    	Wrapper<AigcApp> queryWrapper = Wrappers.<AigcApp>lambdaQuery()
                .like(StrUtil.isNotBlank(data.getName()), AigcApp::getName, data.getName())
                .like(StrUtil.isNotBlank(data.getDes()), AigcApp::getDes, data.getDes())
                .eq(data.getModelId() != null && data.getModelId() > 0, AigcApp::getModelId, data.getModelId())
                .eq(data.getId() != null && data.getId() > 0, AigcApp::getId, data.getId())
                .eq(data.getCover() != null, AigcApp::getCover, data.getCover());
    	return queryWrapper;
    }

    private AigcApp convertToVO(AigcApp app) {
        if (app == null) return null;
        
        AigcApp vo = new AigcApp();
        BeanUtil.copyProperties(app, vo);
        
        // 获取模型
        if (app.getModelId() != null && app.getModelId() > 0) {
            AigcModel model = aigcModelMapper.selectById(app.getModelId());
            if (model != null) {
                vo.setModel(model);  
            }
        }
        
        // 获取知识
        List<AigcKnowledge> knowledges = aigcKnowledgeMapper.selectList(
            Wrappers.<AigcKnowledge>lambdaQuery()
                .eq(AigcKnowledge::getAppId, app.getId())
        );
        vo.setKnowledges(knowledges);
        
        // 获取知识ID
        List<Integer> knowledgeIds = knowledges.stream()
            .map(AigcKnowledge::getId)
            .collect(Collectors.toList());
        vo.setKnowledgeIds(knowledgeIds);
        
        return vo;
    }
    
    @Override
    public AigcApp getById(Integer id) {
        AigcApp app = baseMapper.selectById(id);

        return convertToVO(app);
    }

    @Override
    public boolean save(AigcApp entity) {
        boolean saved = super.save(entity);
        if (saved && entity.getKnowledgeIds() != null) {
            entity.getKnowledgeIds().forEach(kid ->
                aigcKnowledgeMapper.update(Wrappers.<AigcKnowledge>lambdaUpdate()
                    .eq(AigcKnowledge::getId, kid)
                    .set(AigcKnowledge::getAppId, entity.getId())));
        }
        return saved;
    }

    @Override
    public boolean updateById(AigcApp entity) {
        boolean updated = super.updateById(entity);
        if (updated && entity.getKnowledgeIds() != null) {
            // 先清除旧的绑定
        	aigcKnowledgeMapper.update(Wrappers.<AigcKnowledge>lambdaUpdate()
                .eq(AigcKnowledge::getAppId, entity.getId())
                .set(AigcKnowledge::getAppId, null));

            // 再绑定新的知识库
            entity.getKnowledgeIds().forEach(kid ->
            	aigcKnowledgeMapper.update(Wrappers.<AigcKnowledge>lambdaUpdate()
                    .eq(AigcKnowledge::getId, kid)
                    .set(AigcKnowledge::getAppId, entity.getId())));
        }
        return updated;
    }
}
