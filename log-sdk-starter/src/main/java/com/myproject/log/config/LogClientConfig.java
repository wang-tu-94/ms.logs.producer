package com.myproject.log.config;

import com.myproject.log.proto.LogServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.inject.GrpcClientBean;
import org.springframework.context.annotation.Configuration;

@Configuration
// Cette annotation indique au starter gRPC de créer un bean pour ce client spécifique
@GrpcClientBean(
        clazz = LogServiceGrpc.LogServiceStub.class,
        beanName = "logServiceStub",
        client = @GrpcClient("log-ingestor")
)
public class LogClientConfig {
    // Le starter gRPC va générer automatiquement le Bean 'logServiceStub'
    // basé sur les configurations "grpc.client.log-ingestor.*"
}
