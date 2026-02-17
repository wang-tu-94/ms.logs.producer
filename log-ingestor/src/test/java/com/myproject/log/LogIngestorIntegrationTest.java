package com.myproject.log;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = {"app-logs"})
class LogIngestorIntegrationTest {
    @Test
    void contextLoads() {
        // Vérifie que le serveur démarre bien avec gRPC + Kafka
    }
}