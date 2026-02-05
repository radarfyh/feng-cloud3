//package work.metanet.feng.admin.handler;
//
//import java.sql.CallableStatement;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//import org.apache.ibatis.type.BaseTypeHandler;
//import org.apache.ibatis.type.JdbcType;
//import org.apache.ibatis.type.MappedTypes;
//import org.postgresql.util.PGobject;
//
//import cn.hutool.json.JSONObject;
//import cn.hutool.json.JSONUtil;
//
//@MappedTypes(JSONObject.class)
//public class PgJsonTypeHandler extends BaseTypeHandler<JSONObject> {
//
//    @Override
//    public void setNonNullParameter(PreparedStatement ps, int i, 
//                                   JSONObject parameter, JdbcType jdbcType) throws SQLException {
//        PGobject pgObject = new PGobject();
//        pgObject.setType("json");
//        pgObject.setValue(parameter.toString());
//        ps.setObject(i, pgObject);
//    }
//
//    @Override
//    public JSONObject getNullableResult(ResultSet rs, String columnName) throws SQLException {
//        return parseJson(rs.getString(columnName));
//    }
//
//    @Override
//    public JSONObject getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
//        return parseJson(rs.getString(columnIndex));
//    }
//
//    @Override
//    public JSONObject getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
//        return parseJson(cs.getString(columnIndex));
//    }
//
//    private JSONObject parseJson(String jsonStr) {
//        return JSONUtil.parseObj(jsonStr);
//    }
//}
