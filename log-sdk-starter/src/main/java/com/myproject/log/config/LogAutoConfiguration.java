package com.myproject.log.config;

import com.myproject.log.aspect.LogAspect;
import com.myproject.log.client.LogGrpcClient;
import com.myproject.log.proto.LogServiceGrpc;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnClass(LogServiceGrpc.class)
@Import(LogClientConfig.class) // Charge la config du client gRPC
public class LogAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public LogGrpcClient logGrpcClient(LogServiceGrpc.LogServiceStub logServiceStub) {
        return new LogGrpcClient(logServiceStub);
    }

    @Bean
    @ConditionalOnMissingBean
    public LogAspect logAspect(LogGrpcClient logGrpcClient) {
        return new LogAspect(logGrpcClient); // On injecte le client dans l'aspect désormais
    }
}