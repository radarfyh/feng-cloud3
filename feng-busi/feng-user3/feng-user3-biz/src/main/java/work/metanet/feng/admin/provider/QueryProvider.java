package work.metanet.feng.admin.provider;

import work.metanet.feng.admin.api.dto.QueryConditionDTO;
import work.metanet.feng.admin.api.dto.QueryThemeDetail;

import java.util.List;

public abstract class QueryProvider{
    public abstract Integer transFieldType(String field);
    public abstract String getTotalCount(String sql);
    public abstract String createQuerySQLPage(long current,long size,String sql);
    public abstract String createQuerySQL(List<QueryConditionDTO> conditionFields, QueryThemeDetail queryThemeDetail);
}