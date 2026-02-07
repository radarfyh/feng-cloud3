package ltd.huntinginfo.feng.ai.utils;

import java.util.regex.Pattern;

/**
 * 转义工具类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

public class PropertyEscapeUtils {

    /**
     * 转义字符串中的特殊符号，防止被 Spring Boot 自动解析
     * @param input 原始字符串
     * @return 转义后的字符串
     */
    public static String escapeSpringProperties(String input) {
        if (input == null) {
            return null;
        }

        // 需要转义的字符和模式
        String[] patterns = {
            "\\$\\{",   // ${...}
            "\\{\\{",   // {{...}}
            "\\(\\(",   // ((...))
            "\\{",      // {
            "\\(",      // (
            "\\$"       // $
        };

        String[] replacements = {
            "\\\\\\$\\\\\\{",   // \$\{
            "\\\\\\{\\\\\\{",  // \{\{
            "\\\\\\(\\\\\\(",   // \(\(
            "\\\\\\{",          // \{
            "\\\\\\(",         // \(
            "\\\\\\$"           // \$
        };

        String result = input;
        for (int i = 0; i < patterns.length; i++) {
            result = result.replaceAll(patterns[i], replacements[i]);
        }

        return result;
    }

    /**
     * 反转义字符串，恢复原始内容
     * @param input 转义后的字符串
     * @return 原始字符串
     */
    public static String unescapeSpringProperties(String input) {
        if (input == null) {
            return null;
        }

        // 需要反转义的字符和模式
        String[] patterns = {
            "\\\\\\$\\\\\\{",   // \$\{
            "\\\\\\{\\\\\\{",  // \{\{
            "\\\\\\(\\\\\\(",   // \(\(
            "\\\\\\{",         // \{
            "\\\\\\(",          // \(
            "\\\\\\$"           // \$
        };

        String[] replacements = {
            "\\${",   // ${
            "\\{\\{", // {{
            "\\(\\(", // ((
            "\\{",    // {
            "\\(",    // (
            "\\$"     // $
        };

        String result = input;
        for (int i = 0; i < patterns.length; i++) {
            result = result.replaceAll(Pattern.quote(patterns[i]), replacements[i]);
        }

        return result;
    }
    
    public static void test() {
        String original = "This is a ${variable} and {{another}} and ((third))";
        
        // 转义
        String escaped = PropertyEscapeUtils.escapeSpringProperties(original);
        System.out.println("Escaped: " + escaped);
        // 输出: This is a \$\{variable\} and \{\{another\}\} and \(\(third\)\)
        
        // 反转义
        String unescaped = PropertyEscapeUtils.unescapeSpringProperties(escaped);
        System.out.println("Unescaped: " + unescaped);
        // 输出: This is a ${variable} and {{another}} and ((third))
    }
}

