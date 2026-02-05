package work.metanet.feng.common.core.util;

import org.springframework.util.StringUtils;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

/**
 * 年龄计算工具类
 * <p>
 * 该类提供了根据身份证号码和出生日期计算年龄的方法。
 * </p>
 */
public class AgeUtils {

    /**
     * 根据身份证号码计算年龄
     * <p>
     * 该方法通过身份证号码中的出生日期计算年龄。
     * </p>
     *
     * @param psptNo 身份证号码
     * @return 计算得到的年龄，如果身份证号码无效则返回 0
     */
    public static int getAgeByPsptNo(String psptNo) {
        if (!StringUtils.hasLength(psptNo) || psptNo.length() < 14) {
            return 0;  // 身份证号为空或格式错误时，返回0
        }

        String birthDay = psptNo.substring(6, 14);  // 提取出生日期（YYYYMMDD）
        Calendar birthCalendar = Calendar.getInstance();
        birthCalendar.set(
                Integer.parseInt(birthDay.substring(0, 4)),  // 年
                Integer.parseInt(birthDay.substring(4, 6)) - 1,  // 月（从0开始）
                Integer.parseInt(birthDay.substring(6, 8))   // 日
        );

        // 当前日期
        Calendar currentCalendar = Calendar.getInstance();
        int age = currentCalendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR);

        // 如果今年还没过生日，则减去1
        if (currentCalendar.get(Calendar.MONTH) < birthCalendar.get(Calendar.MONTH) ||
            (currentCalendar.get(Calendar.MONTH) == birthCalendar.get(Calendar.MONTH) && currentCalendar.get(Calendar.DAY_OF_MONTH) < birthCalendar.get(Calendar.DAY_OF_MONTH))) {
            age--;
        }

        return age;
    }

    /**
     * 根据出生日期计算年龄
     * <p>
     * 该方法通过传入的出生日期（格式为 yyyy-MM-dd）计算年龄。
     * </p>
     *
     * @param birthday 生日（格式：yyyy-MM-dd）
     * @return 计算得到的年龄，如果日期格式不正确则返回 0
     */
    public static int getAgeByBirthday(String birthday) {
        if (!StringUtils.hasLength(birthday)) {
            return 0;  // 生日为空时，返回0
        }
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
        	LocalDateTime birthDate = LocalDateTime.parse(birthday, format);  // 解析出生日期
            Calendar birthCalendar = Calendar.getInstance();
            birthCalendar.set(birthDate.getYear(), birthDate.getMonthValue(), birthDate.getDayOfMonth());

            // 当前日期
            Calendar currentCalendar = Calendar.getInstance();
            int age = currentCalendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR);

            // 如果今年生日还没到，年龄减1
            if (currentCalendar.get(Calendar.MONTH) < birthCalendar.get(Calendar.MONTH) ||
                (currentCalendar.get(Calendar.MONTH) == birthCalendar.get(Calendar.MONTH) && currentCalendar.get(Calendar.DAY_OF_MONTH) < birthCalendar.get(Calendar.DAY_OF_MONTH))) {
                age--;
            }

            return age;

        } catch (DateTimeException e) {
            e.printStackTrace();  // 打印异常堆栈
            return 0;  // 解析失败时返回 0
        }
    }
}
