package com.leesh.devlab.external.implementation.naver.client;

import com.leesh.devlab.external.implementation.naver.dto.NaverMemberInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(url = "https://openapi.naver.com", name = "naverApiClient")
public interface NaverApiClient {

    @GetMapping(value = "/v1/nid/me", consumes = "application/json")
    NaverMemberInfo requestMemberInfo(@RequestHeader("Content-type") String contentType,
                                      @RequestHeader("Authorization") String accessToken);
}