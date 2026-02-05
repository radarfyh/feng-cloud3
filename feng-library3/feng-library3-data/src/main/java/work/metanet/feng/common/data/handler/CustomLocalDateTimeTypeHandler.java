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
//public class CustomLocalDateTimeTypeHandler extends BaseTypeHandler<LocalDateTime> {
//    private static final String DATETIME_FORMATTER = "yyyy-MM-dd HH:mm:ss";
//    private static final String DATETIME2_FORMATTER = "yyyy-MM-ddTHH:mm";
//    private static final String DATE_FORMATTER = "yyyy-MM-dd";
//
//    @Override
//    public void setNonNullParameter(PreparedStatement ps, int i, 
//                                  LocalDateTime parameter, JdbcType jdbcType) throws SQLException {
//        // 存储时统一转为完整格式
//        ps.setString(i, parameter.format(DateTimeFormatter.ofPattern(DATETIME_FORMATTER)));
//    }
//
//    @Override
//    public LocalDateTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
//        String dateString = rs.getString(columnName);
//        LocalDateTime ret = parseDateTime(dateString);
//        
//        return ret;
//    }
//
//    @Override
//    public LocalDateTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
//        String dateString = rs.getString(columnIndex);
//        return parseDateTime(dateString);
//    }
//
//    @Override
//    public LocalDateTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
//        String dateString = cs.getString(columnIndex);
//        return parseDateTime(dateString);
//    }
//
//    private LocalDateTime parseDateTime(String dateString) {
//        if (dateString == null || dateString.trim().isEmpty()) {
//            return null;
//        }
//
//        // 尝试解析为 yyyy-MM-dd 格式
//        if (dateString.length() == 10 && dateString.charAt(4) == '-' && dateString.charAt(7) == '-') {
//            try {
//                return LocalDateTime.parse(dateString + " 00:00:00", DateTimeFormatter.ofPattern(DATETIME_FORMATTER));
//            } catch (DateTimeParseException e) {
//                return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(DATE_FORMATTER));
//            }
//        } else if (dateString.length() == 16 && dateString.charAt(10) == 'T' && dateString.charAt(13) == ':') {
//            try {
//                return LocalDateTime.parse(dateString + ":00", DateTimeFormatter.ofPattern(DATETIME_FORMATTER));
//            } catch (DateTimeParseException e) {
//                return LocalDateTime.parse(dateString.substring(0, 9), DateTimeFormatter.ofPattern(DATE_FORMATTER));
//            }
//        }
//
//        // 尝试解析为完整格式
//        try {
//            return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(DATETIME_FORMATTER));
//        } catch (DateTimeParseException e1) {
//            try {
//                // 尝试兼容带T的ISO格式
//                return LocalDateTime.parse(dateString);
//            } catch (DateTimeParseException e2) {
//                throw new IllegalArgumentException(
//                    "Could not parse date: '" + dateString + "'. " +
//                    "Supported formats: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss");
//            }
//        }
//    }
//}