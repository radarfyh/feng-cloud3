//package ltd.huntinginfo.feng.center.service.impl;
//
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.stereotype.Service;
//import ltd.huntinginfo.feng.center.api.entity.MsgAuthLog;
//import ltd.huntinginfo.feng.center.mapper.MsgAuthLogMapper;
//import ltd.huntinginfo.feng.center.service.MsgAuthLogService;
//
//import java.util.List;
//
//@Slf4j
//@Service
//public class MsgAuthLogServiceImpl 
//    extends ServiceImpl<MsgAuthLogMapper, MsgAuthLog> 
//    implements MsgAuthLogService {
//
//    @Override
//    public IPage<MsgAuthLog> page(IPage page, MsgAuthLog dto) {
//        LambdaQueryWrapper<MsgAuthLog> wrapper = buildQueryWrapper(dto);
//        return baseMapper.selectPage(page, wrapper);
//    }
//
//    @Override
//    public List<MsgAuthLog> list(MsgAuthLog dto) {
//        LambdaQueryWrapper<MsgAuthLog> wrapper = buildQueryWrapper(dto);
//        return baseMapper.selectList(wrapper);
//    }
//
//    @Override
//    public boolean save(MsgAuthLog dto) {
//        return super.save(dto);
//    }
//
//    @Override
//    public boolean saveBatch(List<MsgAuthLog> dtos) {
//        List<MsgAuthLog> entities = dtos;
//        return super.saveBatch(entities);
//    }
//
//    private LambdaQueryWrapper<MsgAuthLog> buildQueryWrapper(MsgAuthLog dto) {
//        LambdaQueryWrapper<MsgAuthLog> wrapper = new LambdaQueryWrapper<>();
//        if (dto != null) {
//            wrapper.eq(StringUtils.isNotBlank(dto.getAppKey()), MsgAuthLog::getAppKey, dto.getAppKey())
//                   .eq(dto.getStatus() != null, MsgAuthLog::getStatus, dto.getStatus());
//        }
//        return wrapper;
//    }
//}
