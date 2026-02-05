package work.metanet.feng.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import work.metanet.feng.admin.api.entity.PrmCustomerRelation;
import work.metanet.feng.common.data.datascope.FengBaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface PrmCustomerRelationMapper extends FengBaseMapper<PrmCustomerRelation> {
    
    /**
     * 根据客户ID查询所有关系
     */
    @Select("SELECT * FROM prm_customer_relation WHERE customer_id = #{customerId} AND del_flag = '0'")
    List<PrmCustomerRelation> selectByCustomerId(Integer customerId);
    
    /**
     * 查询两个客户间的关系
     */
    @Select("SELECT * FROM prm_customer_relation " +
            "WHERE ((customer_id = #{customerId} AND related_customer_id = #{relatedCustomerId}) " +
            "OR (customer_id = #{relatedCustomerId} AND related_customer_id = #{customerId})) " +
            "AND del_flag = '0'")
    List<PrmCustomerRelation> selectRelationBetweenCustomers(
            @Param("customerId") Integer customerId, 
            @Param("relatedCustomerId") Integer relatedCustomerId);
    
    /**
     * 批量更新关系强度
     */
    @Update("<script>" +
            "UPDATE prm_customer_relation SET relation_strength = CASE id " +
            "<foreach collection='list' item='item'>" +
            "WHEN #{item.id} THEN #{item.relationStrength} " +
            "</foreach>" +
            "END WHERE id IN " +
            "<foreach collection='list' item='item' open='(' separator=',' close=')'>" +
            "#{item.id}" +
            "</foreach>" +
            "</script>")
    int batchUpdateStrength(List<PrmCustomerRelation> relations);
}