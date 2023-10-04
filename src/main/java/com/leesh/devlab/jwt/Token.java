package com.leesh.devlab.jwt;

public interface Token {

    TokenType getTokenType();

    String getValue();

    long getExpiredAt();

}