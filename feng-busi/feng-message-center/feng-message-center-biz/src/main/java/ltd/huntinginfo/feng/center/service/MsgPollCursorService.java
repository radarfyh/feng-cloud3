//package ltd.huntinginfo.feng.center.service;
//
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.extension.service.IService;
//import ltd.huntinginfo.feng.center.api.entity.MsgPollCursor;
//
//import java.util.List;
//
///**
// * 轮询游标表 服务接口
// */
//public interface MsgPollCursorService extends IService<MsgPollCursor> {
//
//    /**
//     * 根据ID查询游标详情
//     */
//    MsgPollCursor getById(String id);
//
//    /**
//     * 分页查询游标列表
//     */
//    IPage<MsgPollCursor> page(IPage<MsgPollCursor> page, MsgPollCursor msgPollCursor);
//
//    /**
//     * 查询游标列表
//     */
//    List<MsgPollCursor> list(MsgPollCursor msgPollCursor);
//
//    /**
//     * 新增游标
//     */
//    boolean save(MsgPollCursor msgPollCursor);
//
//    /**
//     * 更新游标
//     */
//    boolean updateById(MsgPollCursor msgPollCursor);
//
//    /**
//     * 删除游标
//     */
//    boolean removeById(String id);
//
//	/**
//	 * 根据应用标识获取游标（使用默认游标键）
//	 */
//	MsgPollCursor getByAppKey(String appKey);
//
//	/**
//	 * 获取或创建游标（如果不存在则创建）
//	 */
//	MsgPollCursor getOrCreateCursor(String appKey, String cursorKey, Integer pollInterval);
//
//	/**
//	 * 记录轮询错误
//	 */
//	boolean recordPollError(String appKey, String cursorKey, String errorMessage);
//
//	/**
//	 * 记录轮询成功
//	 */
//	boolean recordPollSuccess(String appKey, String cursorKey, String newYbid, int messageCount);
//
//	/**
//	 * 获取所有运行中的游标列表
//	 */
//	List<MsgPollCursor> getRunningCursors();
//
//	/**
//	 * 重置游标（清空游标值和错误信息）
//	 */
//	boolean resetCursor(String appKey, String cursorKey);
//}