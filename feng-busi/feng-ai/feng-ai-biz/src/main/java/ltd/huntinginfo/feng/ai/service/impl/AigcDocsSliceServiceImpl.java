package ltd.huntinginfo.feng.ai.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.huntinginfo.feng.ai.api.entity.AigcDocsSlice;
import ltd.huntinginfo.feng.ai.mapper.AigcDocsSliceMapper;
import ltd.huntinginfo.feng.ai.service.AigcDocsSliceService;

/**
 * 文档切片管理-服务实现类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@RequiredArgsConstructor
@Service
@Slf4j
public class AigcDocsSliceServiceImpl extends ServiceImpl<AigcDocsSliceMapper, AigcDocsSlice> implements AigcDocsSliceService {

	final private AigcDocsSliceMapper aigcDocsSliceMapper;

	@Override
	public List<AigcDocsSlice> list(AigcDocsSlice data) {
        List<AigcDocsSlice> list = baseMapper.selectList(Wrappers.<AigcDocsSlice>lambdaQuery()
                .like(StrUtil.isNotBlank(data.getName()), AigcDocsSlice::getName, data.getName())
        		.orderByDesc(AigcDocsSlice::getCreateTime));
        return list;
	}

    /**
     * 添加文档切片
     * <p>
     * 该方法用于添加文档切片数据，并计算字数及设置状态。
     * </p>
     * 
     * @param data 文档切片实体
     */
    @Override
    @Transactional
    public void addDocsSlice(AigcDocsSlice data) {
        data.setWordNum(data.getContent().length())
            .setIsEmbedding(true);
        try {
            aigcDocsSliceMapper.insert(data);
            log.info("Document slice added with ID: {}", data.getId());
        } catch (Exception e) {
            log.error("Error while adding document slice: {}", data, e);
            throw e;
        }
    }

    /**
     * 更新文档切片
     * <p>
     * 该方法用于更新现有的文档切片数据。
     * </p>
     * 
     * @param data 文档切片实体
     */
    @Override
    @Transactional
    public void updateDocsSlice(AigcDocsSlice data) {
        try {
            aigcDocsSliceMapper.updateById(data);
            log.info("Document slice updated with ID: {}", data.getId());
        } catch (Exception e) {
            log.error("Error while updating document slice: {}", data, e);
            throw e;
        }
    }

    /**
     * 获取文档切片的 Vector ID 列表
     * <p>
     * 该方法根据文档 ID 获取所有切片的 Vector ID。
     * </p>
     * 
     * @param docsId 文档ID
     * @return Vector ID 列表
     */
    @Override
    public List<Integer> listSliceVectorIdsOfDoc(Integer docsId) {
        LambdaQueryWrapper<AigcDocsSlice> selectWrapper = Wrappers.<AigcDocsSlice>lambdaQuery()
                .select(AigcDocsSlice::getEmbedStoreId)
                .eq(AigcDocsSlice::getDocsId, docsId);
        
        // 使用 Collectors.toList() 来收集流中的元素到列表中
        List<Integer> vectorIds = aigcDocsSliceMapper.selectList(selectWrapper)
                .stream()
                .map(AigcDocsSlice::getEmbedStoreId)
                .collect(Collectors.toList()); // 修改这里，使用 Collectors.toList()
        
        log.debug("Slices of doc [{}], count: [{}]", docsId, vectorIds.size());
        return vectorIds;
    }
}
