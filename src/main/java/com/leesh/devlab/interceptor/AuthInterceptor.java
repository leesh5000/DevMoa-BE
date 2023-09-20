package com.leesh.devlab.interceptor;

import com.leesh.devlab.constant.TokenType;
import com.leesh.devlab.jwt.AuthToken;
import com.leesh.devlab.jwt.AuthTokenService;
import com.leesh.devlab.jwt.dto.MemberInfo;
import com.leesh.devlab.jwt.implementation.Jwt;
import com.leesh.devlab.util.HttpHeaderUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthTokenService authTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 1. Authorization Header 추출
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 2. Authorization Header 에서 액세스 토큰 추출 후 JWT 객체 생성
        String accessToken = HttpHeaderUtils.extractToken(authorization);
        AuthToken jwt = new Jwt(TokenType.ACCESS, accessToken);

        // 3. 올바른 토큰인지 검증
        authTokenService.validateAuthToken(jwt, TokenType.ACCESS);

        // 4. 토큰으로부터 유저 정보 추출
        MemberInfo memberInfo = authTokenService.extractMemberInfo(jwt);

        // 5. 유저 정보를 Request에 저장
        request.setAttribute(MemberInfo.class.getName(), memberInfo);

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}