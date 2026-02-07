package ltd.huntinginfo.feng.ai.service;

import cn.hutool.core.lang.Dict;

/**
 * 统计服务接口
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

public interface AigcStatisticsSerivce {

	/**
	 * 当月消息统计
	 */
	Dict request30Chart();

	/**
	 *  查询所有统计数据
	 */
	Dict home();

	/**
	 * 查询请求数量统计
	 */
	Dict requestChart();

	/**
	 * 查询令牌数量统计
	 */
	Dict tokenChart();

	/**
	 * 当月令牌数量统计
	 */
	Dict token30Chart();

}
