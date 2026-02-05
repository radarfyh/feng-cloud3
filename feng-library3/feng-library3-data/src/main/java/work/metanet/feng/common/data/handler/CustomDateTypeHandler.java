//package work.metanet.feng.common.data.handler;
//
//import org.apache.ibatis.type.BaseTypeHandler;
//import org.apache.ibatis.type.JdbcType;
//import org.apache.ibatis.type.MappedJdbcTypes;
//import org.apache.ibatis.type.MappedTypes;
//
//import lombok.extern.slf4j.Slf4j;
//
//import java.sql.CallableStatement;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.time.format.DateTimeParseException;
//import java.util.Date;
//
//@MappedTypes(value = {LocalDateTime.class})
//@MappedJdbcTypes(value = {JdbcType.DATE, JdbcType.TIMESTAMP})
//@Slf4j
//public class CustomDateTypeHandler extends BaseTypeHandler<Date> {
//    private static final String DATETIME_FORMATTER = "yyyy-MM-dd HH:mm:ss";
//    private static final String DATETIME2_FORMATTER = "yyyy-MM-ddTHH:mm";
//    private static final String DATE_FORMATTER = "yyyy-MM-dd";
//
//    @Override
//    public void setNonNullParameter(PreparedStatement ps, int i, 
//    		Date parameter, JdbcType jdbcType) throws SQLException {
//        // 存储时统一转为完整格式
//        ps.setString(i, parameter.toString());
//    }
//
//    @Override
//    public Date getNullableResult(ResultSet rs, String columnName) throws SQLException {
//       
//        return rs.getDate(columnName);
//    }
//
//    @Override
//    public Date getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
//
//        return rs.getDate(columnIndex);
//    }
//
//    @Override
//    public Date getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
//        return cs.getDate(columnIndex);
//    }
//
//
//}