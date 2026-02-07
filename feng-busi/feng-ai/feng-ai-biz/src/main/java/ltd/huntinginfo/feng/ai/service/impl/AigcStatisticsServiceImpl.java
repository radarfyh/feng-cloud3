package ltd.huntinginfo.feng.ai.service.impl;

import cn.hutool.core.lang.Dict;
import ltd.huntinginfo.feng.admin.api.feign.RemoteUserService;
import ltd.huntinginfo.feng.ai.mapper.AigcAppMapper;
import ltd.huntinginfo.feng.ai.mapper.AigcKnowledgeMapper;
import ltd.huntinginfo.feng.ai.mapper.AigcMessageMapper;
import ltd.huntinginfo.feng.ai.service.AigcStatisticsSerivce;
import ltd.huntinginfo.feng.common.core.constant.SecurityConstants;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

/**
 * 统计服务实现类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Slf4j
@Service
@AllArgsConstructor
public class AigcStatisticsServiceImpl implements AigcStatisticsSerivce {

    private final AigcMessageMapper aigcMessageMapper;
    private final AigcKnowledgeMapper aigcKnowledgeMapper;
    private final AigcAppMapper aigcAppMapper;
    private final RemoteUserService remoteUserService;

    @Override
    public Dict request30Chart() {
        return aigcMessageMapper.getReqChartBy30();
    }

    @Override
    public Dict token30Chart() {
        return aigcMessageMapper.getTokenChartBy30();
    }

    @Override
    public Dict tokenChart() {
        return aigcMessageMapper.getTokenChart();
    }

    @Override
    public Dict requestChart() {
        return aigcMessageMapper.getReqChart();
    }

    @Override
    public Dict home() {
        // 获取各个统计数据
        Dict reqData = aigcMessageMapper.getCount();
        Dict totalData = aigcMessageMapper.getTotalSum();
        
        // 获取用户数据
        Dict userData = remoteUserService.getCount(SecurityConstants.FROM_IN).getData();

        // 获取知识和提示的数量
        Long totalKnowledge = aigcKnowledgeMapper.selectCount(Wrappers.query());
        Long totalPrompt = aigcAppMapper.selectCount(Wrappers.query());

        // 将数据合并并返回
        Dict result = Dict.create();
        result.putAll(reqData);
        result.putAll(totalData);
        result.putAll(userData);
        result.set("totalKnowledge", totalKnowledge.intValue())
              .set("totalPrompt", totalPrompt.intValue());

        return result;
    }

}

