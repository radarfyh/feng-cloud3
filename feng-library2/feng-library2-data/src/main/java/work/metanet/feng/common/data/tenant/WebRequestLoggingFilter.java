// 全部注释，以便于解决SSE消息无法实时发送给客户端的问题 2025.3.19

/*
 * package work.metanet.feng.common.data.tenant;
 * 
 * import java.io.IOException; import java.nio.charset.StandardCharsets; import
 * java.util.Enumeration; import java.util.LinkedHashSet; import java.util.Set;
 * import java.util.function.Predicate;
 * 
 * import javax.servlet.FilterChain; import javax.servlet.ServletException;
 * import javax.servlet.http.HttpServletRequest; import
 * javax.servlet.http.HttpServletResponse; import
 * javax.servlet.http.HttpSession;
 * 
 * import org.springframework.core.Ordered; import
 * org.springframework.core.annotation.Order; import
 * org.springframework.http.HttpHeaders; import
 * org.springframework.http.server.ServletServerHttpRequest; import
 * org.springframework.lang.Nullable; import
 * org.springframework.stereotype.Component; import
 * org.springframework.util.AntPathMatcher; import
 * org.springframework.util.Assert; import org.springframework.util.StringUtils;
 * import org.springframework.web.filter.OncePerRequestFilter; import
 * org.springframework.web.util.ContentCachingResponseWrapper; import
 * org.springframework.web.util.WebUtils;
 * 
 * import lombok.extern.slf4j.Slf4j;
 * 
 *//**
	 * WebRequestLoggingFilter 用于记录HTTP请求和响应的详细日志信息。
	 * <p>
	 * 该过滤器会记录请求的各种信息，包括请求参数、请求头、请求体、客户端信息等，同时也会记录响应的状态码和响应体内容。
	 * 通过自定义的配置，可以选择性地记录请求和响应的不同部分，如只记录请求头、排除某些URL模式等。
	 * <p>
	 * 日志记录会按照请求的先后顺序进行，确保每个请求的开始和结束都能被正确地记录。
	 * 如果请求包含请求体，且配置了包含请求体的选项，则请求体会被缓存并记录到日志中。
	 * <p>
	 * 此过滤器适用于处理基于Spring的Web应用中的所有HTTP请求，并能输出详细的调试信息，便于开发人员进行调试和监控。
	 * 
	 * <p>
	 * 配置项： - `excludedUrlPatterns`：指定哪些URL模式不记录日志。 -
	 * `includeQueryString`：是否在日志中包含查询字符串。 -
	 * `includeClientInfo`：是否在日志中记录客户端信息（如IP、用户等）。 - `includeHeaders`：是否记录请求头。 -
	 * `includePayload`：是否记录请求体。 - `includeResponseBody`：是否记录响应体。 -
	 * `maxPayloadLength`：请求体日志的最大长度。 - `maxResponseBodyLength`：响应体日志的最大长度。
	 * </p>
	 *
	 * @author edison
	 */
/*
 * @Slf4j
 * 
 * @Component
 * 
 * @Order(Ordered.HIGHEST_PRECEDENCE) public class WebRequestLoggingFilter
 * extends OncePerRequestFilter {
 * 
 * // 默认的请求日志前缀和后缀 public static final String DEFAULT_BEFORE_MESSAGE_PREFIX =
 * "Before request ["; public static final String DEFAULT_BEFORE_MESSAGE_SUFFIX
 * = "]"; public static final String DEFAULT_AFTER_MESSAGE_PREFIX =
 * "After request ["; public static final String DEFAULT_AFTER_MESSAGE_SUFFIX =
 * "]";
 * 
 * // 默认的请求体和响应体最大长度 private static final int DEFAULT_MAX_PAYLOAD_LENGTH = 100;
 * 
 * // 排除日志记录的URL模式集合 private Set<String> excludedUrlPatterns = new
 * LinkedHashSet<>();
 * 
 * // 是否在日志中包含查询字符串 private boolean includeQueryString = true;
 * 
 * // 是否在日志中包含客户端信息（如IP地址） private boolean includeClientInfo = true;
 * 
 * // 是否在日志中包含请求头 private boolean includeHeaders = true;
 * 
 * // 是否在日志中包含请求体 private boolean includePayload = true;
 * 
 * // 是否在日志中包含响应体 private boolean includeResponseBody = true;
 * 
 * // 用于过滤请求头的Predicate
 * 
 * @Nullable private Predicate<String> headerPredicate;
 * 
 * // 日志记录请求体和响应体的最大长度 private int maxPayloadLength =
 * DEFAULT_MAX_PAYLOAD_LENGTH; private int maxResponseBodyLength =
 * DEFAULT_MAX_PAYLOAD_LENGTH;
 * 
 * // 请求日志前缀和后缀 private String beforeMessagePrefix =
 * DEFAULT_BEFORE_MESSAGE_PREFIX; private String beforeMessageSuffix =
 * DEFAULT_BEFORE_MESSAGE_SUFFIX; private String afterMessagePrefix =
 * DEFAULT_AFTER_MESSAGE_PREFIX; private String afterMessageSuffix =
 * DEFAULT_AFTER_MESSAGE_SUFFIX;
 * 
 * private final AntPathMatcher pathMatcher = new AntPathMatcher();
 * 
 * // 覆盖方法，确保即使是异步请求也能处理
 * 
 * @Override protected boolean shouldNotFilterAsyncDispatch() { return false; }
 * 
 *//**
	 * 过滤请求并记录日志的主要方法，记录请求和响应的详细信息。 该方法首先检查是否需要记录请求日志，然后决定是否记录请求体和响应体，
	 * 通过自定义条件控制日志的输出。最终，它将日志信息输出到控制台。
	 * 
	 * @param request     HTTP请求对象
	 * @param response    HTTP响应对象
	 * @param filterChain 过滤器链，用于继续处理请求
	 * @throws ServletException 请求处理异常
	 * @throws IOException      输入输出异常
	 */
/*
 * @Override protected void doFilterInternal(HttpServletRequest request,
 * HttpServletResponse response, FilterChain filterChain) throws
 * ServletException, IOException {
 * 
 * boolean isFirstRequest = !isAsyncDispatch(request); boolean shouldLog =
 * shouldLog(request);
 * 
 * HttpServletRequest requestToUse = request; HttpServletResponse responseToUse
 * = response;
 * 
 * // 如果需要记录请求体，并且是首次请求且没有包装过的请求对象 if (shouldLog && isIncludePayload() &&
 * isFirstRequest && !(request instanceof
 * CachedInputStreamHttpServletRequestWrapper)) { requestToUse = new
 * CachedInputStreamHttpServletRequestWrapper(request); } //
 * 如果需要记录响应体，并且是首次请求且没有包装过的响应对象 if (shouldLog && isIncludeResponseBody() &&
 * isFirstRequest && !(response instanceof ContentCachingResponseWrapper)) {
 * responseToUse = new ContentCachingResponseWrapper(response); }
 * 
 * // 在请求处理前记录日志 if (shouldLog && isFirstRequest) { beforeRequest(requestToUse,
 * getBeforeMessage(requestToUse, responseToUse)); }
 * 
 * try { // 继续处理请求 filterChain.doFilter(requestToUse, responseToUse); } finally
 * { // 在请求处理后记录日志 if (shouldLog && isIncludeResponseBody() &&
 * !isAsyncStarted(requestToUse)) { afterRequest(responseToUse,
 * getAfterMessage(requestToUse, responseToUse)); } } }
 * 
 *//**
	 * 获取请求处理前的日志消息
	 * 
	 * @param request  HTTP请求对象
	 * @param response HTTP响应对象
	 * @return 格式化后的日志消息
	 */
/*
 * protected String getBeforeMessage(HttpServletRequest request,
 * HttpServletResponse response) { return createMessage(request,
 * this.beforeMessagePrefix, this.beforeMessageSuffix); }
 * 
 *//**
	 * 获取请求处理后的日志消息
	 * 
	 * @param request  HTTP请求对象
	 * @param response HTTP响应对象
	 * @return 格式化后的日志消息
	 */
/*
 * protected String getAfterMessage(HttpServletRequest request,
 * HttpServletResponse response) { return createMessage(response,
 * this.afterMessagePrefix, this.afterMessageSuffix); }
 * 
 *//**
	 * 创建请求前的日志消息
	 * 
	 * @param request HTTP请求对象
	 * @param prefix  消息前缀
	 * @param suffix  消息后缀
	 * @return 格式化后的日志消息
	 */
/*
 * private String createMessage(HttpServletRequest request, String prefix,
 * String suffix) { StringBuilder msg = new StringBuilder(); msg.append(prefix);
 * msg.append(request.getMethod()).append(" ").append(request.getRequestURI());
 * 
 * // 如果需要，添加查询字符串到日志中 if (isIncludeQueryString()) { String queryString =
 * request.getQueryString(); if (queryString != null) {
 * msg.append('?').append(queryString); } }
 * 
 * // 如果需要，添加客户端信息（如IP地址）到日志中 if (isIncludeClientInfo()) { String client =
 * request.getRemoteAddr(); if (StringUtils.hasLength(client)) {
 * msg.append(", client=").append(client); } HttpSession session =
 * request.getSession(false); if (session != null) {
 * msg.append(", session=").append(session.getId()); } String user =
 * request.getRemoteUser(); if (user != null) {
 * msg.append(", user=").append(user); } }
 * 
 * // 如果需要，添加请求头到日志中 if (isIncludeHeaders()) { HttpHeaders headers = new
 * ServletServerHttpRequest(request).getHeaders(); if (getHeaderPredicate() !=
 * null) { Enumeration<String> names = request.getHeaderNames(); while
 * (names.hasMoreElements()) { String header = names.nextElement(); if
 * (!getHeaderPredicate().test(header)) { headers.set(header, "masked"); } } }
 * msg.append(", headers=").append(headers); }
 * 
 * // 如果需要，添加请求体到日志中 if (isIncludePayload()) { String payload =
 * getMessagePayload(request); if (payload != null) {
 * msg.append(", payload=").append(payload); } }
 * 
 * msg.append(suffix); return msg.toString(); }
 * 
 *//**
	 * 创建响应后的日志消息
	 * 
	 * @param response HTTP响应对象
	 * @param prefix   消息前缀
	 * @param suffix   消息后缀
	 * @return 格式化后的日志消息
	 */
/*
 * private String createMessage(HttpServletResponse response, String prefix,
 * String suffix) { StringBuilder msg = new StringBuilder(); msg.append(prefix);
 * 
 * // 如果需要，添加响应体到日志中 if (isIncludeResponseBody()) { String body =
 * getMessageResponseBody(response); if (body != null) {
 * msg.append("body=").append(body); } } msg.append(suffix); return
 * msg.toString(); }
 * 
 *//**
	 * 获取响应体内容
	 * 
	 * @param response HTTP响应对象
	 * @return 响应体内容
	 */
/*
 * protected String getMessageResponseBody(HttpServletResponse response) {
 * ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(response,
 * ContentCachingResponseWrapper.class); if (wrapper != null) { try { byte[] buf
 * = wrapper.getContentAsByteArray(); if (buf.length > 0) { int length =
 * Math.min(buf.length, getMaxResponseBodyLength()); return new String(buf, 0,
 * length, wrapper.getCharacterEncoding()); } } catch (IOException e) { return
 * "[unknown]"; } finally { try { // 将响应体内容写回响应 wrapper.copyBodyToResponse(); }
 * catch (IOException e) { // 忽略异常 } } } return null; }
 * 
 *//**
	 * 获取请求体内容
	 * 
	 * @param request HTTP请求对象
	 * @return 请求体内容
	 */
/*
 * protected String getMessagePayload(HttpServletRequest request) {
 * CachedInputStreamHttpServletRequestWrapper wrapper =
 * WebUtils.getNativeRequest(request,
 * CachedInputStreamHttpServletRequestWrapper.class); if (wrapper != null) { try
 * { byte[] buf = wrapper.getCachedContent(); if (buf.length > 0) { //
 * 检查字符编码是否为空，若为空则使用默认编码 String encoding = wrapper.getCharacterEncoding(); if
 * (encoding == null) { encoding = StandardCharsets.UTF_8.name(); // 使用 UTF-8
 * 作为默认编码 } // 获取请求体内容，并控制最大长度 int length = Math.min(buf.length,
 * getMaxPayloadLength()); return new String(buf, 0, length, encoding); } }
 * catch (IOException e) { log.error("获取请求体内容时发生异常", e); return "[unknown]"; //
 * 返回 "[unknown]" 表示未知内容 } } return null; // 如果没有缓存内容或无法获取内容，则返回 null }
 * 
 *//**
	 * 判断是否需要记录日志
	 * 
	 * @param request HTTP请求对象
	 * @return 是否记录日志
	 */
/*
 * protected boolean shouldLog(HttpServletRequest request) { return
 * this.excludedUrlPatterns.stream().noneMatch(p -> pathMatcher.match(p,
 * request.getRequestURI())); }
 * 
 *//**
	 * 在请求前记录日志
	 * 
	 * @param request HTTP请求对象
	 * @param message 记录的日志消息
	 */
/*
 * protected void beforeRequest(HttpServletRequest request, String message) {
 * log.info(message); }
 * 
 *//**
	 * 在请求后记录日志
	 * 
	 * @param response HTTP响应对象
	 * @param message  记录的日志消息
	 *//*
		 * protected void afterRequest(HttpServletResponse response, String message) {
		 * log.info(message); }
		 * 
		 * public Set<String> getExcludedUrlPatterns() { return excludedUrlPatterns; }
		 * 
		 * public void setExcludedUrlPatterns(Set<String> excludedUrlPatterns) {
		 * this.excludedUrlPatterns = excludedUrlPatterns; }
		 * 
		 * 
		 * public void setIncludeQueryString(boolean includeQueryString) {
		 * this.includeQueryString = includeQueryString; }
		 * 
		 * protected boolean isIncludeQueryString() { return this.includeQueryString; }
		 * 
		 * public void setIncludeClientInfo(boolean includeClientInfo) {
		 * this.includeClientInfo = includeClientInfo; }
		 * 
		 * protected boolean isIncludeClientInfo() { return this.includeClientInfo; }
		 * 
		 * public void setIncludeHeaders(boolean includeHeaders) { this.includeHeaders =
		 * includeHeaders; }
		 * 
		 * protected boolean isIncludeHeaders() { return this.includeHeaders; }
		 * 
		 * public void setIncludePayload(boolean includePayload) { this.includePayload =
		 * includePayload; }
		 * 
		 * protected boolean isIncludePayload() { return this.includePayload; }
		 * 
		 * public boolean isIncludeResponseBody() { return includeResponseBody; }
		 * 
		 * public void setIncludeResponseBody(boolean includeResponseBody) {
		 * this.includeResponseBody = includeResponseBody; }
		 * 
		 * public void setHeaderPredicate(@Nullable Predicate<String> headerPredicate) {
		 * this.headerPredicate = headerPredicate; }
		 * 
		 * @Nullable protected Predicate<String> getHeaderPredicate() { return
		 * this.headerPredicate; }
		 * 
		 * public void setMaxPayloadLength(int maxPayloadLength) {
		 * Assert.isTrue(maxPayloadLength >= 0,
		 * "'maxPayloadLength' should be larger than or equal to 0");
		 * this.maxPayloadLength = maxPayloadLength; }
		 * 
		 * protected int getMaxPayloadLength() { return this.maxPayloadLength; }
		 * 
		 * public int getMaxResponseBodyLength() { return maxResponseBodyLength; }
		 * 
		 * public void setMaxResponseBodyLength(int maxResponseBodyLength) {
		 * this.maxResponseBodyLength = maxResponseBodyLength; }
		 * 
		 * public void setBeforeMessagePrefix(String beforeMessagePrefix) {
		 * this.beforeMessagePrefix = beforeMessagePrefix; }
		 * 
		 * public void setBeforeMessageSuffix(String beforeMessageSuffix) {
		 * this.beforeMessageSuffix = beforeMessageSuffix; }
		 * 
		 * public void setAfterMessagePrefix(String afterMessagePrefix) {
		 * this.afterMessagePrefix = afterMessagePrefix; }
		 * 
		 * public void setAfterMessageSuffix(String afterMessageSuffix) {
		 * this.afterMessageSuffix = afterMessageSuffix; } }
		 * 
		 * 
		 * 
		 */