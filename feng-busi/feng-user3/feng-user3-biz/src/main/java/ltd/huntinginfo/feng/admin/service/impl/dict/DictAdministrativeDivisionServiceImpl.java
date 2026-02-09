package ltd.huntinginfo.feng.admin.service.impl.dict;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import ltd.huntinginfo.feng.admin.api.entity.dict.DictAdministrativeDivision;
import ltd.huntinginfo.feng.admin.mapper.dict.DictAdministrativeDivisionMapper;
import ltd.huntinginfo.feng.admin.service.dict.DictAdministrativeDivisionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PostConstruct;

@Slf4j
@Service
@CacheConfig(cacheNames = "dict:administrative_division")
public class DictAdministrativeDivisionServiceImpl 
    extends ServiceImpl<DictAdministrativeDivisionMapper, DictAdministrativeDivision> 
    implements DictAdministrativeDivisionService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String ALL_CACHE_KEY = "all";
    private static final long CACHE_EXPIRE_HOURS = 24;

    @Override
    @Cacheable(key = "#code")
    public DictAdministrativeDivision getByCode(String code) {
        try {
            QueryWrapper<DictAdministrativeDivision> wrapper = new QueryWrapper<>();
            wrapper.eq("code", code);
            return getOne(wrapper);
        } catch (Exception e) {
            log.error("获取行政区划失败，code: {}", code, e);
            return null;
        }
    }

    @Override
    @Cacheable(key = "'parent:' + #parentCode")
    public List<DictAdministrativeDivision> getByParentCode(String parentCode) {
        QueryWrapper<DictAdministrativeDivision> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_code", parentCode)
               .orderByAsc("code");
        return baseMapper.selectList(wrapper);
    }

    @Override
    @Cacheable(key = "'level:' + #level")
    public List<DictAdministrativeDivision> getByLevel(Integer level) {
        QueryWrapper<DictAdministrativeDivision> wrapper = new QueryWrapper<>();
        wrapper.eq("level", level)
               .orderByAsc("code");
        return baseMapper.selectList(wrapper);
    }

    @Override
    @Cacheable(key = ALL_CACHE_KEY)
    public List<DictAdministrativeDivision> getAllValidItems() {
        QueryWrapper<DictAdministrativeDivision> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("code");
        return baseMapper.selectList(wrapper);
    }

    @Override
    @CacheEvict(allEntries = true)
    public void refreshCache() {
        // 清空缓存后，下次查询会自动重新加载
    }

    /**
     * 初始化缓存数据
     */
    @PostConstruct
    public void initCache() {
        String lockKey = "lock:dict:administrative_division:init";
        try {
            // 尝试获取分布式锁
            Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 5, TimeUnit.MINUTES);
            if (Boolean.TRUE.equals(locked)) {
                List<DictAdministrativeDivision> allDivisions = this.getAllValidItems();
                if (!CollectionUtils.isEmpty(allDivisions)) {
                    // 缓存所有数据
                    redisTemplate.opsForValue().set(
                        "dict:administrative_division:all", 
                        allDivisions,
                        CACHE_EXPIRE_HOURS, 
                        TimeUnit.HOURS
                    );
                    
                    // 缓存每个code的单独数据
                    allDivisions.forEach(division -> {
                        redisTemplate.opsForValue().set(
                            "dict:administrative_division:" + division.getCode(),
                            division,
                            CACHE_EXPIRE_HOURS,
                            TimeUnit.HOURS
                        );
                        
                        // 缓存按父级分组的数据
                        if (division.getParentCode() != null) {
                            redisTemplate.opsForList().rightPush(
                                "dict:administrative_division:parent:" + division.getParentCode(),
                                division
                            );
                        }
                        
                        // 缓存按级别分组的数据
                        redisTemplate.opsForList().rightPush(
                            "dict:administrative_division:level:" + division.getLevel(),
                            division
                        );
                    });
                }
            }
        } finally {
            // 释放锁
            redisTemplate.delete(lockKey);
        }
    }
}