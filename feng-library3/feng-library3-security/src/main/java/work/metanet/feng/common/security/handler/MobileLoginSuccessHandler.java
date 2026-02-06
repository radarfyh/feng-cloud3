// package work.metanet.feng.common.security.handler;

// import cn.hutool.core.map.MapUtil;
// import cn.hutool.core.util.CharsetUtil;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import work.metanet.feng.common.security.service.FengCustomTokenServices;
// import work.metanet.feng.common.security.util.AuthUtils;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Lazy;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.MediaType;
// import org.springframework.security.authentication.BadCredentialsException;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.oauth2.common.OAuth2AccessToken;
// import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
// import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
// import org.springframework.security.oauth2.provider.*;
// import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestValidator;
// import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import java.io.IOException;
// import java.io.PrintWriter;

// /**
//  * 手机号登录成功后，生成并返回 OAuth2 访问令牌（Access Token）。
//  * <p>
//  * 该类实现了 AuthenticationSuccessHandler 接口，在用户认证成功时调用，并生成 OAuth2 访问令牌返回给客户端。
//  * </p>
//  */
// @Slf4j
// public class MobileLoginSuccessHandler implements AuthenticationSuccessHandler {

//     private static final String BASIC_ = "Basic ";

//     @Autowired
//     private ObjectMapper objectMapper;

//     @Autowired
//     private PasswordEncoder passwordEncoder;

//     @Autowired
//     private ClientDetailsService clientDetailsService;

//     @Lazy
//     @Autowired
//     private FengCustomTokenServices tokenServices;

//     /**
//      * 用户认证成功后调用该方法，生成并返回 OAuth2 访问令牌。
//      * <p>
//      * 1. 解析请求头中的 client 信息。
//      * 2. 校验 clientId 和 clientSecret。
//      * 3. 生成 OAuth2 访问令牌，并返回给客户端。
//      * </p>
//      * 
//      * @param request 请求信息
//      * @param response 响应信息
//      * @param authentication 认证信息
//      */
//     @Override
//     public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//             Authentication authentication) {

//         // 获取请求头中的 Authorization 信息
//         String header = request.getHeader(HttpHeaders.AUTHORIZATION);

//         // 如果请求头为空或不包含 "Basic" 前缀，抛出认证失败异常
//         if (header == null || !header.startsWith(BASIC_)) {
//             throw new UnapprovedClientAuthenticationException("请求头中client信息为空");
//         }

//         try {
//             // 解析 Authorization 头，提取 clientId 和 clientSecret
//             String[] tokens = AuthUtils.extractAndDecodeHeader(header);
//             if (tokens.length != 2) {
//                 throw new InvalidClientException("Invalid client credentials");
//             }
//             String clientId = tokens[0];
//             log.debug("onAuthenticationSuccess --> header: {}, tokens: {}, clientId: {}", header, tokens, clientId);

//             // 加载 ClientDetails 对象
//             ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);

//             // 校验 clientSecret
//             if (!passwordEncoder.matches(tokens[1], clientDetails.getClientSecret())) {
//                 throw new InvalidClientException("Given client ID does not match authenticated client");
//             }

//             // 创建 TokenRequest 对象，并进行 Scope 校验
//             TokenRequest tokenRequest = new TokenRequest(MapUtil.newHashMap(), clientId, clientDetails.getScope(),
//                     "mobile");
//             new DefaultOAuth2RequestValidator().validateScope(tokenRequest, clientDetails);

//             // 创建 OAuth2Request 和 OAuth2Authentication 对象
//             OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);
//             OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);

//             // 生成 OAuth2 访问令牌
//             OAuth2AccessToken oAuth2AccessToken = tokenServices.createAccessToken(oAuth2Authentication);
//             log.info("获取token 成功：{}", oAuth2AccessToken.getValue());

//             // 设置响应的编码和内容类型，返回访问令牌
//             response.setCharacterEncoding(CharsetUtil.UTF_8);
//             response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//             PrintWriter printWriter = response.getWriter();
//             printWriter.append(objectMapper.writeValueAsString(oAuth2AccessToken));
//         } catch (IOException e) {
//             throw new BadCredentialsException("Failed to decode basic authentication token", e);
//         }
//     }
// }
