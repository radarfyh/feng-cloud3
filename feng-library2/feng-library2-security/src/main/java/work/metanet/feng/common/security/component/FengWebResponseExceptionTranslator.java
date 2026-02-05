package work.metanet.feng.common.security.component;

import work.metanet.feng.common.security.util.FengSecurityMessageSourceUtil;
import work.metanet.feng.common.security.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.DefaultThrowableAnalyzer;
import org.springframework.security.oauth2.common.exceptions.ClientAuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.web.util.ThrowableAnalyzer;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.util.Locale;

/**
 * OAuth2 异常处理类，重写默认的异常转换逻辑
 * <p>
 * 本类实现了 WebResponseExceptionTranslator 接口，用于处理 OAuth2 认证过程中抛出的异常，
 * 根据不同的异常类型进行相应的处理，并返回相应的响应信息。
 * </p>
 */
@Slf4j
public class FengWebResponseExceptionTranslator implements WebResponseExceptionTranslator {

    // 异常分析器，用于分析异常链
    private final ThrowableAnalyzer throwableAnalyzer = new DefaultThrowableAnalyzer();

    /**
     * 处理 OAuth2 异常
     * <p>
     * 该方法根据不同类型的异常返回相应的 HTTP 错误响应。支持认证失败、权限不足、令牌无效等多种错误类型。
     * </p>
     *
     * @param e 异常对象
     * @return 返回 OAuth2Exception 异常的响应体
     */
    @Override
    public ResponseEntity<OAuth2Exception> translate(Exception e) {
        // 获取异常链中的所有原因
        Throwable[] causeChain = throwableAnalyzer.determineCauseChain(e);

        // 检查认证异常并处理
        Exception ase = (AuthenticationException) throwableAnalyzer.getFirstThrowableOfType(AuthenticationException.class, causeChain);
        if (ase != null) {
            return handleOAuth2Exception(new FengUnauthorizedException(e.getMessage(), e));
        }

        // 检查访问权限异常并处理
        ase = (AccessDeniedException) throwableAnalyzer.getFirstThrowableOfType(AccessDeniedException.class, causeChain);
        if (ase != null) {
            return handleOAuth2Exception(new FengForbiddenException(ase.getMessage(), ase));
        }

        // 检查无效授权异常并处理
        ase = (InvalidGrantException) throwableAnalyzer.getFirstThrowableOfType(InvalidGrantException.class, causeChain);
        if (ase != null) {
            String msg = FengSecurityMessageSourceUtil.getAccessor().getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials", ase.getMessage(), Locale.CHINA);
            return handleOAuth2Exception(new FengInvalidException(msg, ase));
        }

        // 检查方法不支持异常并处理
        ase = (HttpRequestMethodNotSupportedException) throwableAnalyzer.getFirstThrowableOfType(HttpRequestMethodNotSupportedException.class, causeChain);
        if (ase != null) {
            return handleOAuth2Exception(new FengMethodNotAllowedException(ase.getMessage(), ase));
        }

        // 处理不合法的令牌错误
        ase = (InvalidTokenException) throwableAnalyzer.getFirstThrowableOfType(InvalidTokenException.class, causeChain);
        if (ase != null) {
            return handleOAuth2Exception(new FengTokenInvalidException(ase.getMessage(), ase));
        }

        // 处理通用 OAuth2 错误
        ase = (OAuth2Exception) throwableAnalyzer.getFirstThrowableOfType(OAuth2Exception.class, causeChain);
        if (ase != null) {
            return handleOAuth2Exception((OAuth2Exception) ase);
        }

        // 处理未知的错误并返回 500 错误
        return handleOAuth2Exception(new FengServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), e));
    }

    /**
     * 处理 OAuth2Exception 错误并构建响应
     * <p>
     * 该方法根据错误类型构建响应实体，返回给客户端相应的错误信息。
     * </p>
     *
     * @param e OAuth2Exception 错误对象
     * @return 返回包含 OAuth2Exception 的响应实体
     */
    private ResponseEntity<OAuth2Exception> handleOAuth2Exception(OAuth2Exception e) {
        int status = e.getHttpErrorCode();
        // 如果是客户端认证异常，直接返回该异常，否则封装为 FengAuth2Exception
        if (e instanceof ClientAuthenticationException) {
            return new ResponseEntity<>(e, HttpStatus.valueOf(status));
        }
        return new ResponseEntity<>(new FengAuth2Exception(e.getMessage(), e.getOAuth2ErrorCode()),
                HttpStatus.valueOf(status));
    }
}
