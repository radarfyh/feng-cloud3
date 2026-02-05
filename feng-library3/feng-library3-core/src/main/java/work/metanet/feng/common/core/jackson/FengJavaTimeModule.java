package work.metanet.feng.common.core.jackson;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.PackageVersion;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Java 8 时间序列化模块
 * <p>
 * 该类用于为 Java 8 中的时间类型（如 LocalDateTime、LocalDate 和 LocalTime）提供序列化和反序列化的规则。
 * </p>
 */
public class FengJavaTimeModule extends SimpleModule {

    private static final String DATE_TIME_PATTERN = DatePattern.NORM_DATETIME_PATTERN; // yyyy-MM-dd HH:mm:ss
    private static final String DATE_PATTERN = DatePattern.NORM_DATE_PATTERN; // yyyy-MM-dd
    private static final String TIME_PATTERN = DatePattern.NORM_TIME_PATTERN; // HH:mm:ss

    /**
     * 构造函数
     * <p>
     * 该构造函数用于初始化模块并添加 Java 8 时间类型的序列化和反序列化规则。
     * </p>
     */
    public FengJavaTimeModule() {
        super(PackageVersion.VERSION);

        // 时间序列化规则
        addDateTimeSerializers();

        // 时间反序列化规则
        addDateTimeDeserializers();
    }

    /**
     * 添加时间的序列化规则
     * <p>
     * 该方法用于为 LocalDateTime、LocalDate 和 LocalTime 添加序列化规则。
     * </p>
     */
    private void addDateTimeSerializers() {
        // LocalDateTime 序列化规则
        this.addSerializer(LocalDateTime.class, 
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));

        // LocalDate 序列化规则
        this.addSerializer(LocalDate.class, 
                new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_PATTERN)));

        // LocalTime 序列化规则
        this.addSerializer(LocalTime.class, 
                new LocalTimeSerializer(DateTimeFormatter.ofPattern(TIME_PATTERN)));
    }

    /**
     * 添加时间的反序列化规则
     * <p>
     * 该方法用于为 LocalDateTime、LocalDate 和 LocalTime 添加反序列化规则。
     * </p>
     */
    private void addDateTimeDeserializers() {
        // LocalDateTime 反序列化规则
        this.addDeserializer(LocalDateTime.class, 
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));

        // LocalDate 反序列化规则
        this.addDeserializer(LocalDate.class, 
                new LocalDateDeserializer(DateTimeFormatter.ofPattern(DATE_PATTERN)));

        // LocalTime 反序列化规则
        this.addDeserializer(LocalTime.class, 
                new LocalTimeDeserializer(DateTimeFormatter.ofPattern(TIME_PATTERN)));
    }
}
