package ltd.huntinginfo.feng.admin.service.impl.dict;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import ltd.huntinginfo.feng.admin.api.entity.dict.GovAgency;
import ltd.huntinginfo.feng.admin.mapper.dict.GovAgencyMapper;
import ltd.huntinginfo.feng.admin.service.dict.GovAgencyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;

@Slf4j
@Service
@CacheConfig(cacheNames = "dict:agency")
public class GovAgencyServiceImpl 
    extends ServiceImpl<GovAgencyMapper, GovAgency> 
    implements GovAgencyService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String ALL_CACHE_KEY = "all";
    private static final String TREE_CACHE_KEY = "tree";
    private static final long CACHE_EXPIRE_HOURS = 24;

    @Override
    @Cacheable(key = "#code")
    public GovAgency getByCode(String code) {
        try {
            QueryWrapper<GovAgency> wrapper = new QueryWrapper<>();
            wrapper.eq("code", code);
            return getOne(wrapper);
        } catch (Exception e) {
            log.error("获取机构信息失败，code: {}", code, e);
            return null;
        }
    }

    @Override
    @Cacheable(key = "'parent:' + #parentCode")
    public List<GovAgency> getByParentCode(String parentCode) {
        QueryWrapper<GovAgency> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_code", parentCode)
               .orderByAsc("code");
        return baseMapper.selectList(wrapper);
    }

    @Override
    @Cacheable(key = "'level:' + #level")
    public List<GovAgency> getByLevel(Integer level) {
        QueryWrapper<GovAgency> wrapper = new QueryWrapper<>();
        wrapper.eq("level", level)
               .orderByAsc("code");
        return baseMapper.selectList(wrapper);
    }

    @Override
    @Cacheable(key = TREE_CACHE_KEY)
    public List<GovAgency> getAgencyTree() {
        List<GovAgency> allAgencies = this.getAllValidItems();
        Map<String, List<GovAgency>> agencyMap = allAgencies.stream()
            .collect(Collectors.groupingBy(agency -> 
                agency.getParentCode() == null ? "root" : agency.getParentCode()));
        
        List<GovAgency> rootAgencies = agencyMap.get("root");
        if (rootAgencies != null) {
            rootAgencies.forEach(root -> buildTree(root, agencyMap));
            return rootAgencies;
        }
        return Collections.emptyList();
    }

    private void buildTree(GovAgency parent, Map<String, List<GovAgency>> agencyMap) {
        List<GovAgency> children = agencyMap.get(parent.getCode());
        if (children != null) {
            parent.setChildren(children);
            children.forEach(child -> buildTree(child, agencyMap));
        }
    }

    @Override
    @Cacheable(key = ALL_CACHE_KEY)
    public List<GovAgency> getAllValidItems() {
        QueryWrapper<GovAgency> wrapper = new QueryWrapper<>();
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
        String lockKey = "lock:dict:agency:init";
        try {
            // 尝试获取分布式锁
            Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 5, TimeUnit.MINUTES);
            if (Boolean.TRUE.equals(locked)) {
                List<GovAgency> allAgencies = this.getAllValidItems();
                if (!CollectionUtils.isEmpty(allAgencies)) {
                    // 缓存所有数据
                    redisTemplate.opsForValue().set(
                        "dict:agency:all", 
                        allAgencies,
                        CACHE_EXPIRE_HOURS, 
                        TimeUnit.HOURS
                    );
                    
                    // 缓存每个code的单独数据
                    allAgencies.forEach(agency -> {
                        redisTemplate.opsForValue().set(
                            "dict:agency:" + agency.getCode(),
                            agency,
                            CACHE_EXPIRE_HOURS,
                            TimeUnit.HOURS
                        );
                        
                        // 缓存按父级分组的数据
                        if (agency.getParentCode() != null) {
                            redisTemplate.opsForList().rightPush(
                                "dict:agency:parent:" + agency.getParentCode(),
                                agency
                            );
                        }
                        
                        // 缓存按级别分组的数据
                        redisTemplate.opsForList().rightPush(
                            "dict:agency:level:" + agency.getLevel(),
                            agency
                        );
                    });
                    
                    // 缓存树形结构
                    redisTemplate.opsForValue().set(
                        "dict:agency:tree",
                        this.getAgencyTree(),
                        CACHE_EXPIRE_HOURS,
                        TimeUnit.HOURS
                    );
                }
            }
        } finally {
            // 释放锁
            redisTemplate.delete(lockKey);
        }
    }
}