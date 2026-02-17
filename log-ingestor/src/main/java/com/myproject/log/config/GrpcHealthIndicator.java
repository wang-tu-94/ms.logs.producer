package com.myproject.log.config;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class GrpcHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        // On pourrait vérifier ici si le serveur gRPC est en train de tourner
        boolean isRunning = true; // À lier à ton instance de serveur gRPC si besoin

        if (isRunning) {
            return Health.up().withDetail("port", 9090).build();
        }
        return Health.down().withDetail("reason", "gRPC server not responding").build();
    }
}