package work.metanet.feng.common.data.tenant;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

/**
 * HttpServletRequestWrapper的扩展，用于缓存请求体内容
 * <p>
 * 该类允许多次读取HttpServletRequest请求体，避免重复读取。
 * </p>
 */
public class CachedInputStreamHttpServletRequestWrapper extends HttpServletRequestWrapper {

    // 缓存请求体内容
    private byte[] cachedContent;

    /**
     * 构造函数，初始化并缓存请求体内容
     * @param request HttpServletRequest 对象
     * @throws IOException 如果读取请求体时发生I/O错误
     */
    public CachedInputStreamHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        cacheBody(request);
    }

    /**
     * 缓存请求体内容
     * <p>
     * 根据请求的Content-Type，判断请求数据类型，并分别处理表单请求和其他类型的请求。
     * </p>
     *
     * @param request HttpServletRequest 对象
     * @throws IOException 如果读取请求体时发生I/O错误
     */
    private void cacheBody(HttpServletRequest request) throws IOException {
        String contentType = request.getContentType();
        boolean isFormRequest = !StringUtils.isEmpty(contentType)
                && (contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE) 
                    || contentType.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE));
        
        // 处理表单请求
        if (isFormRequest) {
            String bodyString = "";
            Map<String, String[]> parameterMap = request.getParameterMap();
            if (!CollectionUtils.isEmpty(parameterMap)) {
                // 将表单数据拼接成键值对字符串
                bodyString = parameterMap.entrySet().stream()
                    .map(entry -> {
                        String[] values = entry.getValue();
                        return entry.getKey() + "=" + (values != null ? (values.length == 1 ? values[0] : Arrays.toString(values)) : null);
                    })
                    .collect(Collectors.joining("&"));
            }
            this.cachedContent = bodyString.getBytes();
        } else {
            // 非表单请求，缓存请求体的字节数据
            this.cachedContent = StreamUtils.copyToByteArray(request.getInputStream());
        }
    }

    /**
     * 获取缓存的请求体输入流
     * @return ServletInputStream 对象
     * @throws IOException 如果读取请求体时发生I/O错误
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedContent);
        return new ServletInputStream() {

            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0; // 判断流是否读取完
            }

            @Override
            public boolean isReady() {
                return true; // 表示流随时可以读取
            }

            @Override
            public void setReadListener(ReadListener listener) {
                // 目前不处理异步读取，保持空实现
            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read(); // 从缓存中读取字节
            }
        };
    }

    /**
     * 获取缓存的请求体内容
     * @return 缓存的请求体内容字节数组
     */
    public byte[] getCachedContent() {
        return cachedContent;
    }

    /**
     * 获取缓存的请求体内容的字符流
     * @return BufferedReader 用于读取请求体的字符流
     * @throws IOException 如果读取请求体时发生I/O错误
     */
    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream())); // 使用缓存的输入流
    }
}
