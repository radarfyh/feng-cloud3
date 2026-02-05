package work.metanet.feng.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import work.metanet.feng.admin.api.dto.FollowRecordDTO;
import work.metanet.feng.admin.api.entity.PrmFollowRecord;
import work.metanet.feng.admin.api.vo.FollowRecordVO;

import java.util.List;

public interface PrmFollowRecordService {

    /**
     * 分页查询跟踪记录
     */
    IPage<FollowRecordVO> pageFollowRecord(Page<PrmFollowRecord> page, FollowRecordDTO dto);
    /**
     * 查询跟踪记录列表
     */
    List<FollowRecordVO> listFollowRecord(FollowRecordDTO dto);
    /**
     * 获取跟踪记录详情
     */
    FollowRecordVO getFollowRecordDetail(Integer id);

    /**
     * 新增跟踪记录
     */
    void saveFollowRecord(FollowRecordDTO dto);

    /**
     * 修改跟踪记录
     */
    void updateFollowRecord(FollowRecordDTO dto);

    /**
     * 删除跟踪记录
     */
    void removeFollowRecord(Integer id);

    /**
     * 查询客户的所有跟踪记录
     */
    List<FollowRecordVO> listByCustomerId(Integer customerId);

    /**
     * 查询联系人的所有跟踪记录
     */
    List<FollowRecordVO> listByContactId(Integer contactId);


}