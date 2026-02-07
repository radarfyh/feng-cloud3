package ltd.huntinginfo.feng.ai.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import ltd.huntinginfo.feng.ai.api.entity.AigcDocsSlice;

/**
 * AI文档切片服务接口
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

public interface AigcDocsSliceService extends IService<AigcDocsSlice> {

	List<AigcDocsSlice> list(AigcDocsSlice data);

	/**
	 * 更新文档切片
	 * <p>
	 * 该方法用于更新现有的文档切片数据。
	 * </p>
	 * 
	 * @param data 文档切片实体
	 */
	void updateDocsSlice(AigcDocsSlice data);

	/**
	 * 获取文档切片的 Vector ID 列表
	 * <p>
	 * 该方法根据文档 ID 获取所有切片的 Vector ID。
	 * </p>
	 * 
	 * @param docsId 文档ID
	 * @return Vector ID 列表
	 */
	List<Integer> listSliceVectorIdsOfDoc(Integer docsId);

	/**
	 * 添加文档切片
	 * <p>
	 * 该方法用于添加文档切片数据，并计算字数及设置状态。
	 * </p>
	 * 
	 * @param data 文档切片实体
	 */
	void addDocsSlice(AigcDocsSlice data);

}
