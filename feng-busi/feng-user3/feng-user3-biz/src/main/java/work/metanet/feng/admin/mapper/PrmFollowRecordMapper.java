package work.metanet.feng.admin.mapper;

import java.util.Date;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import work.metanet.feng.admin.api.dto.FollowRecordDTO;
import work.metanet.feng.admin.api.entity.PrmFollowRecord;
import work.metanet.feng.admin.api.vo.FollowRecordVO;
import work.metanet.feng.common.data.datascope.FengBaseMapper;

@Mapper
public interface PrmFollowRecordMapper extends FengBaseMapper<PrmFollowRecord> {

    /**
     * 分页查询跟踪记录
     */
    Page<FollowRecordVO> pageFollowRecord(Page<FollowRecordDTO> page, FollowRecordDTO dto);

    /**
     * 根据客户ID统计跟踪记录数量
     */
    int countByCustomerId(Integer customerId);

    /**
     * 获取客户最后跟踪时间
     */
    Date getLastFollowTimeByCustomer(Integer customerId);
}