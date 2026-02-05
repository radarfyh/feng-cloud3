package work.metanet.feng.admin.api.dto;

import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "查询字段")
public class QueryField {
    /**字段ID*/
    @Schema(description = "字段ID")
    private String fieldId;
    /**表ID*/
    @Schema(description = "表ID")
    private String tableId;
    /**字段名*/
    @Schema(description = "字段名",required = true)
    private String fieldName;
    /**字段中文名*/
    @Schema(description = "字段中文名")
    private String fieldNameChinese;
    /**表英文名*/
    @Schema(description = "表英文名",required = true)
    private String tableName;
    /**表中文名*/
    @Schema(description = "表中文名")
    private String tableNameChinese;
    /**字段别名*/
    @Schema(description = "字段别名")
    private String alias;
    @Schema(description = "排序 升序:ASC ,降序:DESC 默认为升序")
    private String orderBy;
    @Builder.Default
    @Schema(description = " 且:and ,或:or 默认为且")
    private String logic = "and";
    public String generateFullName(){
        return tableName+"."+fieldName;
    }
    @Schema(description = "前括号(")
    private String prefixBracket;
    @Schema(description = "后括号)")
    private String suffixBracket;

    public String generateFullNameAlisa(){
        StringBuffer sb = new StringBuffer();
        sb.append(tableName).append(".").append(fieldName);
        if(StrUtil.isNotEmpty(alias)){
            sb.append(" as ").append(alias);
        }
        return sb.toString();
    }

    public String generateOrderBy(){
        StringBuffer sb = new StringBuffer(tableName);
        sb.append(".").append(fieldName).append(" ");
        if("desc".equalsIgnoreCase(orderBy)) {
            sb.append(orderBy);
        }else{
            sb.append("asc");
        }
        return sb.toString();
    }

}
