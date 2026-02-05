package work.metanet.feng.common.core.util;

import cn.hutool.core.util.StrUtil;

/**
 * 脱敏工具类
 * <p>
 * 提供对各种敏感信息的脱敏方法，例如：姓名、身份证号、手机号、银行卡号等。
 * </p>
 */
public class DesensitizedUtils {

    /**
     * 对字符串进行脱敏操作
     * <p>
     * 该方法保留字符串的前后指定字符，其他部分用遮罩字符替代。
     * </p>
     *
     * @param origin           原始字符串
     * @param prefixNoMaskLen 左侧需要保留几位明文字段
     * @param suffixNoMaskLen 右侧需要保留几位明文字段
     * @param maskStr         用于遮罩的字符串，如 '*'
     * @return 脱敏后的结果
     */
    public static String desValue(String origin, int prefixNoMaskLen, int suffixNoMaskLen, String maskStr) {
        if (origin == null || origin.length() == 0) {
            return origin; // 如果原始字符串为空，直接返回
        }

        int totalLength = origin.length();
        StringBuilder sb = new StringBuilder();
        // 保留前缀
        sb.append(origin.substring(0, Math.min(prefixNoMaskLen, totalLength)));

        // 中间部分使用遮罩字符
        int maskLen = totalLength - prefixNoMaskLen - suffixNoMaskLen;
        if (maskLen > 0) {
            for (int i = 0; i < maskLen; i++) {
                sb.append(maskStr);
            }
        }

        // 保留后缀
        if (suffixNoMaskLen > 0 && totalLength > prefixNoMaskLen) {
            sb.append(origin.substring(totalLength - suffixNoMaskLen));
        }

        return sb.toString();
    }

    /**
     * 【中文姓名】只显示最后一个汉字，其他隐藏为星号，比如：**梦
     *
     * @param fullName 姓名
     * @return 脱敏后的姓名
     */
    public static String chineseName(String fullName) {
        if (fullName == null) {
            return null;
        }
        return desValue(fullName, 0, 1, "*");
    }

    /**
     * 【身份证号】显示前六位, 后四位，其他隐藏。共计18位或者15位，比如：340304*******1234
     *
     * @param id 身份证号码
     * @return 脱敏后的身份证号
     */
    public static String idCardNum(String id) {
        return desValue(id, 6, 4, "*");
    }

    /**
     * 【固定电话】后四位，其他隐藏，比如 ****1234
     *
     * @param num 固定电话
     * @return 脱敏后的固定电话
     */
    public static String fixedPhone(String num) {
        return desValue(num, 0, 4, "*");
    }

    /**
     * 【手机号码】前三位，后四位，其他隐藏，比如135****6810
     *
     * @param num 手机号码
     * @return 脱敏后的手机号码
     */
    public static String mobilePhone(String num) {
        return desValue(num, 3, 4, "*");
    }

    /**
     * 【地址】只显示到地区，不显示详细地址，比如：北京市海淀区****
     *
     * @param address 地址
     * @return 脱敏后的地址
     */
    public static String address(String address) {
        return desValue(address, 6, 0, "*");
    }

    /**
     * 【电子邮箱】邮箱前缀仅显示第一个字母，前缀其他隐藏，用星号代替，@及后面的地址显示，
     * 比如：d**@126.com
     *
     * @param email 电子邮箱
     * @return 脱敏后的邮箱
     */
    public static String email(String email) {
        if (email == null) {
            return null;
        }
        int index = StrUtil.indexOf(email, '@');
        if (index <= 1) {
            return email;
        }
        String preEmail = desValue(email.substring(0, index), 1, 0, "*");
        return preEmail + email.substring(index);
    }

    /**
     * 【银行卡号】前六位，后四位，其他用星号隐藏每位1个星号，比如：622260**********1234
     *
     * @param cardNum 银行卡号
     * @return 脱敏后的银行卡号
     */
    public static String bankCard(String cardNum) {
        return desValue(cardNum, 6, 4, "*");
    }

    /**
     * 【密码】密码的全部字符都用*代替，比如：******
     *
     * @param password 密码
     * @return 脱敏后的密码
     */
    public static String password(String password) {
        if (password == null) {
            return null;
        }
        return "******";
    }

    /**
     * 【密钥】密钥除了最后三位，全部都用*代替，比如：***xdS 脱敏后长度为6，如果明文长度不足三位，
     * 则按实际长度显示，剩余位置补*
     *
     * @param key 密钥
     * @return 脱敏后的密钥
     */
    public static String key(String key) {
        if (key == null) {
            return null;
        }
        int viewLength = 6;
        StringBuilder tmpKey = new StringBuilder(desValue(key, 0, 3, "*"));
        if (tmpKey.length() > viewLength) {
            return tmpKey.substring(tmpKey.length() - viewLength);
        } else if (tmpKey.length() < viewLength) {
            int buffLength = viewLength - tmpKey.length();
            for (int i = 0; i < buffLength; i++) {
                tmpKey.insert(0, "*");
            }
            return tmpKey.toString();
        } else {
            return tmpKey.toString();
        }
    }
}
