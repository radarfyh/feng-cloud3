package work.metanet.feng.common.security.util;

import cn.hutool.core.codec.Base64;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

/**
 * 认证授权相关的工具类，提供从请求头中提取并解码授权信息的方法。
 * <p>
 * 该类封装了与认证授权过程相关的常用工具方法，如从请求头提取并解码基本认证信息。
 * </p>
 */
@Slf4j
@UtilityClass
public class AuthUtils {

    // 基本认证类型常量
    private static final String BASIC_PREFIX = "Basic ";

    /**
     * 从请求头中提取并解码基本认证信息。
     * <p>
     * 从Authorization头中提取Base64编码的clientId和clientSecret，解码后返回。
     * </p>
     *
     * @param header 请求头中的Authorization字段
     * @return 返回解析后的clientId和clientSecret
     * @throws RuntimeException 如果请求头格式错误或无法解码
     */
    @SneakyThrows
    public String[] extractAndDecodeHeader(String header) {
        if (header == null || !header.startsWith(BASIC_PREFIX)) {
            throw new RuntimeException("请求头中缺少有效的 Basic 认证信息");
        }

        // 获取Base64编码的token并解码
        byte[] base64Token = header.substring(BASIC_PREFIX.length()).getBytes(StandardCharsets.UTF_8);
        byte[] decoded;
        try {
            decoded = Base64.decode(base64Token);
        } catch (IllegalArgumentException e) {
            log.error("Basic认证信息解码失败", e);
            throw new RuntimeException("无法解码基本认证信息");
        }

        String token = new String(decoded, StandardCharsets.UTF_8);

        // 分割clientId和clientSecret
        int delim = token.indexOf(":");
        if (delim == -1) {
            throw new RuntimeException("无效的Basic认证信息，未能正确分割clientId和clientSecret");
        }

        return new String[] { token.substring(0, delim), token.substring(delim + 1) };
    }

    /**
     * 从HTTP请求中提取并解码Basic认证信息。
     * <p>
     * 该方法首先检查请求头中的Authorization字段，若该字段存在且为Basic认证信息，
     * 则提取并解码其中的clientId和clientSecret。
     * </p>
     *
     * @param request HTTP请求对象
     * @return 返回解析后的clientId和clientSecret
     * @throws RuntimeException 如果请求头格式错误或无法解码
     */
    @SneakyThrows
    public String[] extractAndDecodeHeader(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 检查是否存在有效的Authorization头
        if (header == null || !header.startsWith(BASIC_PREFIX)) {
            log.error("请求头中的client信息为空或格式不正确");
            throw new RuntimeException("请求头中client信息为空或格式不正确");
        }

        return extractAndDecodeHeader(header);
    }
}
