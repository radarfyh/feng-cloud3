package ltd.huntinginfo.feng.agent.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import cn.hutool.core.lang.Dict;
import ltd.huntinginfo.feng.agent.api.entity.AusAuthLog;

@Mapper
public interface AusAuthLogMapper extends BaseMapper<AusAuthLog> {
    /**
     * 按月统计请求数量
     * @param appType 应用类型代码
     * @return 按月统计结果
     */
    @Select(
        "SELECT \n" +
        "    DATE_FORMAT(create_time, '%Y-%m') AS month,\n" +
        "    COUNT(*) AS count\n" +
        "FROM \n" +
        "    t_aus_auth_log\n" +
        "WHERE \n" +
        "    create_time >= DATE_SUB(CURRENT_DATE(), INTERVAL 1 YEAR)\n" +
        "    AND request_app_type = #{appType}" +
        "GROUP BY \n" +
        "    DATE_FORMAT(create_time, '%Y-%m')\n" +
        "ORDER BY \n" +
        "    month ASC"
    )
    Dict getMonthReq(@Param("appType") String appType);
    
    /**
     * 统计总请求数、今日请求数
     * @param appType 应用类型代码
     * @return 统计结果
     */
    @Select(
        "SELECT \n" +
        "    COUNT(*) AS totalReq,\n" +
        "    COUNT(IF(DATE(create_time) = CURRENT_DATE, 1, NULL)) AS curReq\n" +
        "FROM \n" +
        "    t_aus_auth_log \n" +
        "WHERE \n" +
        "    request_app_type = #{appType}"
    )
    Dict getReqCount(@Param("appType") String appType);
}
