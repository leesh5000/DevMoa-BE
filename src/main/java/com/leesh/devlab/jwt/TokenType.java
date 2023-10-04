package com.leesh.devlab.jwt;

import lombok.Getter;

@Getter
public enum TokenType {

    ACCESS(20 * 60 * 10000), // 20분
    REFRESH(7 * 24 * 60 * 60 * 1000) // 7일
    ;

    // 토큰의 유효 기간 (밀리 초)
    private final long expiresInMills;

    TokenType(Integer expiresIn) {
        this.expiresInMills = expiresIn;
    }

    public static boolean isAccessToken(Token token) {
        return TokenType.ACCESS == token.getTokenType();
    }

    public static boolean isRefreshToken(Token token) {
        return TokenType.REFRESH == token.getTokenType();
    }

}
