package com.leesh.devlab.global.configuration;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableEncryptableProperties
public class JasyptConfiguration {

    @Value("${jasypt.encryptor.password}")
    private String password;

    @Bean
    public PooledPBEStringEncryptor jasyptStringEncryptor() {

        final String algorithm = "PBEWithMD5AndTripleDES";

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setPoolSize(4);
        encryptor.setPassword(password);
        encryptor.setAlgorithm(algorithm);

        return encryptor;

    }

}