package ltd.huntinginfo.feng.ai.mapper;

import cn.hutool.core.lang.Dict;
import ltd.huntinginfo.feng.ai.api.entity.AigcMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * AI消息映射接口
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Mapper
public interface AigcMessageMapper extends BaseMapper<AigcMessage> {

	/**
	 * 递归日期范围查询
	 * @return
	 */
    @Select(
        "WITH RECURSIVE DateRange AS (\r\n"
        + "    SELECT CURRENT_DATE::date AS date  -- 显式转换为date类型\r\n"
        + "    UNION ALL\r\n"
        + "    SELECT (date - INTERVAL '1 day')::date  -- 确保每次递归也返回date类型\r\n"
        + "    FROM DateRange\r\n"
        + "    WHERE date > (CURRENT_DATE - INTERVAL '31 day')::date\r\n"
        + ")\r\n"
        + "SELECT \r\n"
        + "    d.date, \r\n"
        + "    COALESCE(COUNT(m.*), 0) AS tokens\r\n"
        + "FROM \r\n"
        + "    DateRange d\r\n"
        + "LEFT JOIN \r\n"
        + "    aigc_message m\r\n"
        + "ON \r\n"
        + "    m.create_time::date = d.date  -- 使用相同类型比较\r\n"
        + "    AND m.role = 'assistant'\r\n"
        + "GROUP BY \r\n"
        + "    d.date\r\n"
        + "ORDER BY \r\n"
        + "    d.date DESC;" 
	)
    Dict getReqChartBy30();

    /**
     * 月份统计查询
     * @return
     */
    @Select(
        "SELECT \r\n"
        + "    COALESCE(\r\n"
        + "        SUBSTRING(CAST(create_time AS CHAR) FROM 1 FOR 7),\r\n"
        + "        '0'\r\n"
        + "    ) AS month,\r\n"
        + "    COALESCE(COUNT(*), 0) AS count\r\n"
        + "FROM \r\n"
        + "    aigc_message\r\n"
        + "WHERE \r\n"
        + "    create_time >= CURRENT_DATE - INTERVAL '1 year'\r\n"
        + "    AND role = 'assistant'\r\n"
        + "GROUP BY \r\n"
        + "    month\r\n"
        + "ORDER BY \r\n"
        + "    month ASC;"
    )
    Dict getReqChart();

    /**
     * 递归日期查询
     * @return
     */
    @Select(
        "WITH RECURSIVE DateRange AS (\r\n"
        + "    SELECT CURRENT_DATE::date AS date  -- 显式转换为date类型\r\n"
        + "    UNION ALL\r\n"
        + "    SELECT (date - INTERVAL '1 day')::date  -- 确保类型一致\r\n"
        + "    FROM DateRange\r\n"
        + "    WHERE date > (CURRENT_DATE - INTERVAL '31 day')::date\r\n"
        + ")\r\n"
        + "SELECT \r\n"
        + "    d.date, \r\n"
        + "    COALESCE(SUM(m.tokens), 0) AS tokens\r\n"
        + "FROM \r\n"
        + "    DateRange d\r\n"
        + "LEFT JOIN \r\n"
        + "    aigc_message m\r\n"
        + "ON \r\n"
        + "    m.create_time::date = d.date  -- 使用相同类型比较\r\n"
        + "    AND m.role = 'assistant'\r\n"
        + "GROUP BY \r\n"
        + "    d.date\r\n"
        + "ORDER BY \r\n"
        + "    d.date DESC;"
    )
    Dict getTokenChartBy30();

    @Select(
        "SELECT \r\n"
        + "    COALESCE(\r\n"
        + "        EXTRACT(YEAR FROM create_time) || '-' || LPAD(EXTRACT(MONTH FROM create_time)::text, 2, '0'),\r\n"
        + "        '0'\r\n"
        + "    ) AS month,\r\n"
        + "    COALESCE(SUM(tokens), 0) AS count\r\n"
        + "FROM \r\n"
        + "    aigc_message\r\n"
        + "WHERE \r\n"
        + "    create_time >= CURRENT_DATE - INTERVAL '1 year'\r\n"
        + "    AND role = 'assistant'\r\n"
        + "GROUP BY \r\n"
        + "    EXTRACT(YEAR FROM create_time), EXTRACT(MONTH FROM create_time)\r\n"
        + "ORDER BY \r\n"
        + "    EXTRACT(YEAR FROM create_time), EXTRACT(MONTH FROM create_time);"
    )
    Dict getTokenChart();

    /**
     * 查询：总请求数、今日请求数
     * @return
     */
    @Select(
        "SELECT \r\n"
        + "    COALESCE(COUNT(*), 0) AS totalReq, \r\n"
        + "    COALESCE(SUM(CASE WHEN DATE(create_time) = CURRENT_DATE THEN 1 ELSE 0 END), 0) AS curReq \r\n"
        + "FROM \r\n"
        + "    aigc_message \r\n"
        + "WHERE \r\n"
        + "    role = 'assistant'"
    )
    Dict getCount();

    /**
     * 查询总令牌数和今日令牌数
     * @return
     */
    @Select(
        "SELECT \r\n"
        + "    COALESCE(SUM(tokens), 0) AS totalToken,\r\n"
        + "    COALESCE(SUM(\r\n"
        + "        CASE WHEN CAST(create_time AS DATE) = CURRENT_DATE THEN tokens ELSE 0 END\r\n"
        + "    ), 0) AS curToken\r\n"
        + "FROM \r\n"
        + "    aigc_message;"
    )
    Dict getTotalSum();
}

