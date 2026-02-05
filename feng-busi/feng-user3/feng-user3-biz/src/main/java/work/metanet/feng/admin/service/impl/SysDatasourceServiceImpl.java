package work.metanet.feng.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.creator.DataSourceCreator;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.druid.DruidConfig;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.jdbc.MysqlDataSource;

import work.metanet.feng.admin.api.dto.DataSourceType;
import work.metanet.feng.admin.api.dto.DatasourceSqlDTO;
import work.metanet.feng.admin.api.dto.SysDatasourceDTO;
import work.metanet.feng.admin.api.dto.TableDesc;
import work.metanet.feng.admin.api.entity.SysDatasource;
import work.metanet.feng.admin.api.entity.SysTable;
import work.metanet.feng.admin.api.vo.TableVO;
import work.metanet.feng.admin.mapper.SysDatasourceMapper;
import work.metanet.feng.admin.mapper.SysTableMapper;
import work.metanet.feng.admin.provider.Provider;
import work.metanet.feng.admin.provider.ProviderFactory;
import work.metanet.feng.admin.service.SysDatasourceService;
import work.metanet.feng.common.core.util.R;
import work.metanet.feng.common.core.util.SpringContextHolder;
import work.metanet.feng.common.datasource.util.DsJdbcUrlEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 数据源表(SysDatasource)表服务实现类
 *
 * @author edison
 * @since 2022-05-11 16:04:06
 */
@Slf4j
@Service
@AllArgsConstructor
public class SysDatasourceServiceImpl extends ServiceImpl<SysDatasourceMapper, SysDatasource> implements SysDatasourceService {

    private final StringEncryptor stringEncryptor;

    private final DataSourceCreator druidDataSourceCreator;
    
    private final SysTableMapper sysTableMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public R saveDsByEnc(SysDatasource sysDatasource) {
        //是否url字符串拼接
        sysDatasource = urlConcatenation(sysDatasource);
        // 校验配置合法性
        R r = checkDataSource(sysDatasource);
        if (r.getCode() != 0) return r;
        //去重校验,同一个机构下不允许重复添加相同的数据源
        R checkR = checkDsName(sysDatasource, "1");
        if (checkR.getCode() != 0) return checkR;
        // 更新数据库配置【密码加密】
        sysDatasource.setPassword(stringEncryptor.encrypt(sysDatasource.getPassword()));
        this.baseMapper.insert(sysDatasource);
        return R.ok(sysDatasource.getId());
    }

    /**
     * 添加动态数据源，【需要操作数据库则调用该方法】
     *
     * @param sysDatasource 数据源信息
     */
    @Override
    public void addDynamicDataSource(SysDatasource sysDatasource) {
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        dataSourceProperty.setPoolName(sysDatasource.getName());
        dataSourceProperty.setUrl(sysDatasource.getUrl());
        dataSourceProperty.setUsername(sysDatasource.getUsername());
        dataSourceProperty.setPassword(sysDatasource.getPassword());
        // 增加 ValidationQuery 参数
        DruidConfig druidConfig = new DruidConfig();
        DsJdbcUrlEnum urlEnum = DsJdbcUrlEnum.getByType(sysDatasource.getDbType());
        druidConfig.setValidationQuery(urlEnum.getValidationQuery());
        dataSourceProperty.setDruid(druidConfig);
        DataSource dataSource = druidDataSourceCreator.createDataSource(dataSourceProperty);
        DynamicRoutingDataSource dynamicRoutingDataSource = SpringContextHolder.getBean(DynamicRoutingDataSource.class);
        dynamicRoutingDataSource.addDataSource(dataSourceProperty.getPoolName(), dataSource);
    }


    /**
     * 校验数据源配置是否有效
     *
     * @param sysDatasource 数据源信息
     * @return 有效/无效
     */
    @Override
    public R checkDataSource(SysDatasource sysDatasource) {
        try (Connection connect = DriverManager.getConnection(sysDatasource.getUrl(), sysDatasource.getUsername(), sysDatasource.getPassword())) {
        } catch (SQLException e) {
            log.error("数据源配置 {} , 获取链接失败: {}", sysDatasource.getName(), e.getMessage());
            return R.failed("数据源配置 :" + sysDatasource.getName() + ", 获取链接失败");
        }
        return R.ok();
    }

    /**
     * 更新数据源
     *
     * @param sysDatasource 数据源信息
     * @return
     */
    @Override
    public R updateDsByEnc(SysDatasource sysDatasource) {
        //是否url字符串拼接
        sysDatasource = urlConcatenation(sysDatasource);
        //去重校验,同一个机构下不允许修改重复相同的数据源
        R checkR = checkDsName(sysDatasource, "2");
        if (checkR.getCode() != 0) return checkR;
        // 更新数据库配置
        if (StrUtil.isBlank(sysDatasource.getPassword())) {
            SysDatasource sysDatasourceOne = baseMapper.selectById(sysDatasource.getId());
            if (Objects.isNull(sysDatasourceOne)) return R.failed("参数不存在");
            sysDatasource.setPassword(stringEncryptor.decrypt(sysDatasourceOne.getPassword()));
        }
        // 校验配置合法性
        R r = checkDataSource(sysDatasource);
        if (r.getCode() != 0) return r;
        // 更新数据库配置
        if (StrUtil.isNotBlank(sysDatasource.getPassword())) {
            sysDatasource.setPassword(stringEncryptor.encrypt(sysDatasource.getPassword()));
        }
        this.baseMapper.updateById(sysDatasource);
        return R.ok();
    }

    /**
     * 通过数据源名称删除
     *
     * @param id 数据源ID
     * @return
     */
    @Override
    public Boolean removeByDsId(Integer id) {
        DynamicRoutingDataSource dynamicRoutingDataSource = SpringContextHolder.getBean(DynamicRoutingDataSource.class);
        dynamicRoutingDataSource.removeDataSource(baseMapper.selectById(id).getName());
        this.baseMapper.deleteById(id);
        return Boolean.TRUE;
    }

    @DS("#datasourceSqlDTO.dataBaseName")//使用spel从复杂参数获取
    @Override
    public R dynamicSql(DatasourceSqlDTO datasourceSqlDTO) {
        SysDatasource sysDatasource = new SysDatasource();
        sysDatasource.setDbName(datasourceSqlDTO.getDbName());
        sysDatasource.setUrl(datasourceSqlDTO.getJdbcUrl());
        sysDatasource.setUsername(datasourceSqlDTO.getUsername());
        sysDatasource.setPassword(datasourceSqlDTO.getPassword());
        sysDatasource.setDbType(datasourceSqlDTO.getDbType());
        addDynamicDataSource(sysDatasource);
        try {
            MysqlDataSource mysqlDataSource = new MysqlDataSource();
            mysqlDataSource.setUrl(datasourceSqlDTO.getJdbcUrl());
            mysqlDataSource.setUser(datasourceSqlDTO.getUsername());
            mysqlDataSource.setPassword(stringEncryptor.decrypt(datasourceSqlDTO.getPassword()));
            jdbcTemplate.setDataSource(mysqlDataSource);
            return R.ok(jdbcTemplate.queryForList(datasourceSqlDTO.getSql()));
        } catch (Exception e) {
            log.error("动态SQL查询失败：{}", e.getMessage());
            return R.failed("动态SQL查询失败：" + e.getMessage());
        }
    }

    @Override
    public R testDatasourceById(Integer id) {
        SysDatasource sysDatasource = baseMapper.selectById(id);
        sysDatasource.setPassword(stringEncryptor.decrypt(sysDatasource.getPassword()));
        return checkDataSource(sysDatasource);
    }

    @Override
    public R testDatasource(SysDatasourceDTO sysDatasourceDTO) {
        SysDatasource sysDatasource = BeanUtil.copyProperties(sysDatasourceDTO, SysDatasource.class);
        if (sysDatasourceDTO.getIsEncrypt()) {
            //解密
            sysDatasource.setPassword(stringEncryptor.decrypt(sysDatasource.getPassword()));
        }
        sysDatasource = urlConcatenation(sysDatasource);
        return checkDataSource(sysDatasource);
    }


    /**
     * 校验是否重复添加数据源
     *
     * @param sysDatasource
     * @param type
     * @return
     */
    private R checkDsName(SysDatasource sysDatasource, String type) {
        //JDBC新增
        if ("1".equals(type)) {
            Long count = baseMapper.selectCount(Wrappers.<SysDatasource>lambdaQuery().eq(SysDatasource::getHost, sysDatasource.getHost()).eq(SysDatasource::getPort, sysDatasource.getPort()).eq(SysDatasource::getDbName, sysDatasource.getDbName()).eq(SysDatasource::getOrganCode, sysDatasource.getOrganCode()));
            if (count > 0) return R.failed("该数据源连接已存在,请勿重复添加");
        }
        //JDBC修改
        if ("2".equals(type)) {
            Long count = baseMapper.selectCount(Wrappers.<SysDatasource>lambdaQuery().eq(SysDatasource::getHost, sysDatasource.getHost()).eq(SysDatasource::getPort, sysDatasource.getPort()).eq(SysDatasource::getDbName, sysDatasource.getDbName()).eq(SysDatasource::getOrganCode, sysDatasource.getOrganCode()).ne(SysDatasource::getId, sysDatasource.getId()));
            if (count > 0) return R.failed("该数据源连接已存在,请勿重复修改");
        }
        return R.ok();
    }


    /**
     * jdbcUrlStr 字符串拼接
     *
     * @param sysDatasource
     * @return
     */
    private SysDatasource urlConcatenation(SysDatasource sysDatasource) {
//        DsJdbcUrlEnum urlEnum = DsJdbcUrlEnum.getByType(sysDatasource.getDbType());
    	DsJdbcUrlEnum urlEnum = DsJdbcUrlEnum.getByName(sysDatasource.getDbType());
        String jdbcUrl;
        
        // 根据数据库类型构建基础URL
        switch (sysDatasource.getDbType().toLowerCase()) {
            case "mongodb":
                // mongodb://用户名:密码@IP:端口号/数据库名称
                jdbcUrl = String.format(urlEnum.getUrl(), 
                    sysDatasource.getUsername(),
                    sysDatasource.getPassword(),
                    sysDatasource.getHost(),
                    sysDatasource.getPort(),
                    sysDatasource.getDbName());
                break;
                
            case "elasticsearch":
                jdbcUrl = String.format(urlEnum.getUrl(), 
                    sysDatasource.getHost(),
                    sysDatasource.getPort());
                break;
                
            case "postgresql":
                // 基础URL格式示例：jdbc:postgresql://%s:%d/%s
                jdbcUrl = String.format(urlEnum.getUrl(),
                    sysDatasource.getHost(),
                    sysDatasource.getPort(),
                    sysDatasource.getDbName());
                break;
                
            default: // MySQL/Oracle等通用处理
                jdbcUrl = String.format(urlEnum.getUrl(),
                    sysDatasource.getHost(),
                    sysDatasource.getPort(),
                    sysDatasource.getDbName());
        }

        // 参数拼接处理
        String finalUrl = strSplit(jdbcUrl, sysDatasource.getVarParameter());
        
        // 更新对象属性
        sysDatasource.setUrl(finalUrl);
        
        return sysDatasource;
    }

    /**
     * 安全拼接URL参数
     * @param baseUrl JDBC基础URL
     * @param params 参数对象
     * @return 完整URL
     */
    private String strSplit(String baseUrl, Object params) {
        if (Objects.isNull(params)) {
            return baseUrl;
        }
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.createObjectNode();
            
            // 情况1：参数已经是ObjectNode
            if (params instanceof ObjectNode) {
            	node = (ObjectNode) params;
            } 
            // 情况2：参数是ArrayList（[{key=currentSchema, value=public}]）
            else if (params instanceof ArrayList) {
            	// 转换 为[{"key":"currentSchema","value":"public"}]
            	node = mapper.readTree(mapper.writeValueAsString(params));
            }
            // 情况3：其他类型尝试转换为ObjectNode
            else {
            	node = mapper.convertValue(params, ObjectNode.class);
            }
            
        	//包含问号，已经加了数据库名和配置参数，继续直接拼接varParameter参数
            StringJoiner sj = new StringJoiner("&");
            node.forEach(entry -> {
            	final String[] paramName = {""};
                final String[] paramValue = {""};
            	entry.fields().forEachRemaining(item -> {
	                String key = URLEncoder.encode(item.getKey(), StandardCharsets.UTF_8);
	                if (key.equals("key")) {
	                	paramName[0] = URLEncoder.encode(item.getValue().asText(), StandardCharsets.UTF_8);
	                } 
	                if (key.equals("value")) {
	                	paramValue[0] = URLEncoder.encode(item.getValue().asText(), StandardCharsets.UTF_8);
	                }
            	});
            	if (StrUtil.isNotBlank(paramName[0]) && StrUtil.isNotBlank(paramValue[0])) {
            		sj.add(paramName[0] + "=" + paramValue[0]);
            	}
            });
            
            return baseUrl + (baseUrl.contains("?") ? "&" : "?") + sj;
        } catch (Exception e) {
            log.error("参数拼接失败", e);
            return baseUrl;
        }
    }

    /**
     * 数据源动态参数拼接
     *
     * @param jdbcUrl
     * @param varParameter
     * @return
     */
//    private String StrSplit(String jdbcUrl, Object varParameter) {
//        if (Objects.isNull(varParameter)) {
//            return jdbcUrl;
//        }
//        //包含问号，已经加了数据库名和配置参数，继续直接拼接varParameter参数
//        for (JSONObject jsonObject : JSONUtil.parseArray(varParameter).jsonIter()) {
//            String variable = jsonObject.get("key") + "=" + jsonObject.get("value").toString();
//            if (jdbcUrl.contains(variable)) {
//                continue;
//            }
//            jdbcUrl += "&" + variable;
//        }
////        jdbcUrl = Arrays.stream(jdbcUrl.split("&")).filter(s -> !s.isEmpty()).reduce((a, b) -> a + "&" + b).orElse("");
//        return jdbcUrl;
//    }
    
    @Override
    public List<TableVO> getTables(String id) throws Exception {
        SysDatasource datasource = baseMapper.selectById(id);
        
        Provider datasourceProvider = ProviderFactory.getProvider();
        List<TableDesc> tables = datasourceProvider.getTables(datasource);
        
        Map<String,Object> map = new HashMap<>();
        map.put("datasource_id",id);
        
        List<SysTable> list = sysTableMapper.selectByMap(map);
        Map<String,SysTable> tableMap = list.stream().collect(Collectors.toMap(SysTable::getTableName, table-> table, (k1,k2)->k1));
        
        List<TableVO> ret = new ArrayList<TableVO>();
        for (TableDesc tableDesc : tables) {
            TableVO tableVO = new TableVO();
            tableVO.setDatasourceId(datasource.getId());
            tableVO.setTableName(tableDesc.getName());
            tableVO.setTableComment(tableDesc.getRemark());
            tableVO.setCreateTime(LocalDateTime.now());
            if(null == tableMap.get(tableDesc.getName())) {
            	tableVO.setEnableCheck(false);
            }else {
            	tableVO.setId(tableMap.get(tableDesc.getName()).getId());
            	tableVO.setEnableCheck(true);
            	tableVO.setTableNameChinese(tableMap.get(tableDesc.getName()).getTableNameChinese());
            }
            ret.add(tableVO);
        }
        return ret;
    }
    
    @Override
    public Collection<DataSourceType> types() {
        Collection<DataSourceType> types = new ArrayList<>();
        types.addAll(SpringContextHolder.getApplicationContext().getBeansOfType(DataSourceType.class).values());
        return types;
    }
}