//package ltd.huntinginfo.feng.center.service;
//
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.extension.service.IService;
//import ltd.huntinginfo.feng.center.api.entity.MsgCenterToken;
//
//import java.util.Date;
//import java.util.List;
//
///**
// * 部级Token管理表 服务接口
// */
//public interface MsgCenterTokenService extends IService<MsgCenterToken> {
//
//    /**
//     * 根据ID查询Token详情
//     */
//    MsgCenterToken getById(String id);
//
//    /**
//     * 分页查询Token列表
//     */
//    IPage<MsgCenterToken> page(IPage<MsgCenterToken> page, MsgCenterToken msgCenterToken);
//
//    /**
//     * 查询Token列表
//     */
//    List<MsgCenterToken> list(MsgCenterToken msgCenterToken);
//
//    /**
//     * 新增Token
//     */
//    boolean save(MsgCenterToken msgCenterToken);
//
//    /**
//     * 更新Token
//     */
//    boolean updateById(MsgCenterToken msgCenterToken);
//
//    /**
//     * 删除Token
//     */
//    boolean removeById(String id);
//
//	/**
//	 * 记录请求统计
//	 */
//	boolean recordRequest(String appKey, String apiName, boolean success);
//
//	/**
//	 * 创建或更新Token记录
//	 */
//	MsgCenterToken createOrUpdateToken(String appKey, String centerToken, Date expireTime);
//
//	/**
//	 * 检查Token是否有效（未过期且状态为有效）
//	 */
//	boolean isTokenValid(String appKey);
//
//	/**
//	 * 根据应用标识获取Token
//	 */
//	MsgCenterToken getByAppKey(String appKey);
//}