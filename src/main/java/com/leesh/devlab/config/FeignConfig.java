package com.leesh.devlab.config;

import feign.FeignException;
import feign.Logger;
import feign.RetryableException;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
@EnableFeignClients(basePackages = "com.leesh.devlab")
@Import(FeignClientsConfiguration.class)
@Configuration
public class FeignConfig {

    private final ErrorDecoder errorDecoder = new ErrorDecoder.Default();

    @Bean
    feign.Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(1000, 2000, 3);
    }

    @Bean
    public ErrorDecoder errorDecoder() {

        return (methodKey, response) -> {
            log.error("{} request failed, status = {}, reason = {}", methodKey, response.status(), response.reason());

            FeignException feignException = FeignException.errorStatus(methodKey, response);
            HttpStatus httpStatus = HttpStatus.valueOf(response.status());

            // 만약, 상대 서버 에러라면, 재시도 로직을 진행한다.
            if (httpStatus.is5xxServerError()) {
                return new RetryableException(
                        response.status(),
                        feignException.getMessage(),
                        response.request().httpMethod(),
                        feignException,
                        null,
                        response.request()
                );
            }

            return errorDecoder.decode(methodKey, response);
        };
    }
}
