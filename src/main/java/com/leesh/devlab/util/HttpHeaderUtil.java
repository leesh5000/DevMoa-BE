package com.leesh.devlab.util;

import com.leesh.devlab.exception.ErrorCode;
import com.leesh.devlab.exception.custom.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import static com.leesh.devlab.jwt.GrantType.isBearerType;

public class HttpHeaderUtil {

    // 인스턴스화 방지
    private HttpHeaderUtil() {
    }

    public static String extractAuthorization(HttpServletRequest request) throws AuthException {

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Authorization 헤더 검증
        validateAuthorization(authorization);

        return authorization.split(" ")[1];
    }

    public static void validateAuthorization(String authorization) {

        // Authorization 헤더가 비어있으면 예외 발생
        if (!StringUtils.hasText(authorization)) {
            throw new AuthException(ErrorCode.NOT_EXIST_AUTHORIZATION, "Authorization header is empty");
        }

        String[] authorizations = authorization.split(" ");

        // Authorization 헤더가 Bearer 타입이 아니거나, 토큰이 없으면 예외 발생
        if (authorizations.length != 2) {

            // Bearer 타입이 아니면 예외 발생
            if (!isBearerType(authorizations[0])) {
                throw new AuthException(ErrorCode.INVALID_AUTHORIZATION_HEADER, "Invalid authorization type");
            }

            // Bearer 타입은 맞는데, 토큰이 없는 경우
            throw new AuthException(ErrorCode.NOT_EXIST_TOKEN, "access token is empty");
        }
    }

}