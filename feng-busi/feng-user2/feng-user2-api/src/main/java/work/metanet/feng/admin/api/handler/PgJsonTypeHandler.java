//package work.metanet.feng.admin.api.handler;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//
//import org.apache.ibatis.type.BaseTypeHandler;
//import org.apache.ibatis.type.JdbcType;
//import org.postgresql.util.PGobject;
//
//import java.io.IOException;
//import java.sql.*;
//
//public class PgJsonTypeHandler extends BaseTypeHandler<JsonNode> {
//    private static final PGobject pgObject = new PGobject();
//
//    @Override
//    public void setNonNullParameter(PreparedStatement ps, int i, 
//                                   JsonNode parameter, JdbcType jdbcType) throws SQLException {
//        pgObject.setType("jsonb");
//        pgObject.setValue(parameter.toString());
//        ps.setObject(i, pgObject);
//    }
//
//    @Override
//    public JsonNode getNullableResult(ResultSet rs, String columnName) throws SQLException {
//        return parseJson(rs.getString(columnName));
//    }
//
//    private JsonNode parseJson(String jsonStr) throws SQLException {
//        try {
//            return new ObjectMapper().readTree(jsonStr);
//        } catch (IOException e) {
//            throw new SQLException("JSON解析失败", e);
//        }
//    }
//
//    @Override
//    public JsonNode getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
//        return parseJson(rs.getString(columnIndex));
//    }
//
//    @Override
//    public JsonNode getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
//        return parseJson(cs.getString(columnIndex));
//    }
//
//
//}
