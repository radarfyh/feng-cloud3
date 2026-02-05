package work.metanet.feng.ai.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;

import work.metanet.feng.ai.api.dto.ChatReq;
import work.metanet.feng.ai.api.entity.AigcDocs;
import work.metanet.feng.ai.api.entity.AigcKnowledge.SliceConfig;

/**
 * AI文档服务接口
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

public interface AigcDocsService extends IService<AigcDocs> {
	List<AigcDocs> list(AigcDocs data);
	
	void autoSliceDocument(AigcDocs doc);
	
    void reSliceDocument(Integer docId);
    
    /**
     * 向量相似度搜索
     * @param data
     * @return
     */
	List<Map<String, Object>> search(ChatReq data);
	
	/**
	 * 解析指定文件
	 * @param data 文档参数
	 * @param url 文件链接 
	 */
	void embedDocsSlice(Integer ossId, Integer userId, AigcDocs data, String url, SliceConfig sliceConfig);
	
	/**
	 * 解析文档data下所有文件（关联到OSS）
	 * @param data 文档参数
	 */
	void embedDocsSlice(Integer userId, AigcDocs data, SliceConfig sliceConfig);
	
	void clearDocSlices(Integer docsId);
	/**
	 * 删除文档及其切片
	 * <p>
	 * 该方法删除指定文档及与之关联的所有文档切片。
	 * </p>
	 * 
	 * @param docsId 文档ID
	 */
	void removeSlicesOfDoc(Integer docsId);
	/**
	 * 添加文档
	 * <p>
	 * 该方法用于将文档数据添加到数据库，并设置创建时间。
	 * </p>
	 * 
	 * @param data 文档实体
	 */
	void addDocs(AigcDocs data);
	/**
	 * 更新文档
	 * <p>
	 * 该方法用于更新现有的文档数据。
	 * </p>
	 * 
	 * @param data 文档实体
	 */
	void updateDocs(AigcDocs data);

	void textEmbeddingTask(Integer userId, AigcDocs data, SliceConfig sliceConfig);

	/**
	 * 全文搜索
	 */
	List<Map<String, Object>> fullTextSearch(ChatReq data);

}
