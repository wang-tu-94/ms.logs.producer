package com.myproject.log.service;

import com.myproject.log.proto.LogMessage;
import com.myproject.log.proto.LogSummary;
import io.grpc.internal.testing.StreamRecorder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class LogGrpcServiceTest {

    private LogGrpcService logGrpcService;
    private KafkaTemplate<String, String> kafkaTemplate;

    @BeforeEach
    void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
        logGrpcService = new LogGrpcService(kafkaTemplate, "app-logs");
    }

    @Test
    void should_receive_logs_and_send_to_kafka() throws Exception {
        // 1. Préparer le récepteur de réponse (Server -> Client)
        StreamRecorder<LogSummary> responseObserver = StreamRecorder.create();

        // 2. Initialiser le flux de réception (Client -> Server)
        var requestObserver = logGrpcService.sendLogs(responseObserver);

        // 3. Envoyer 2 messages de log simulés
        LogMessage log1 = LogMessage.newBuilder()
                .setServiceName("test-app")
                .setContent("Hello World")
                .build();

        LogMessage log2 = LogMessage.newBuilder()
                .setServiceName("test-app")
                .setContent("Second Log")
                .build();

        requestObserver.onNext(log1);
        requestObserver.onNext(log2);
        requestObserver.onCompleted();

        // 4. Vérifier la réponse gRPC
        responseObserver.awaitCompletion(5, TimeUnit.SECONDS);
        LogSummary summary = responseObserver.getValues().get(0);

        assertThat(summary.getLogsProcessed()).isEqualTo(2);
        assertThat(summary.getStatus()).isEqualTo("SUCCESS");

        // 5. Vérifier que Kafka a bien reçu les appels
        verify(kafkaTemplate, times(2)).send(eq("app-logs"), anyString(), anyString());
    }
}