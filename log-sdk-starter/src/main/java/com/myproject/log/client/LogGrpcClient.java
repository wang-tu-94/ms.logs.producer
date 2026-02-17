package com.myproject.log.client;

import com.myproject.log.proto.LogMessage;
import com.myproject.log.proto.LogServiceGrpc;
import com.myproject.log.proto.LogSummary;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogGrpcClient {

    private final LogServiceGrpc.LogServiceStub logServiceStub;

    public void emit(LogMessage message) {
        // Ouverture d'un flux de streaming vers l'ingestor
        StreamObserver<LogMessage> requestObserver = logServiceStub.sendLogs(new StreamObserver<LogSummary>() {
            @Override
            public void onNext(LogSummary summary) {
                log.debug("Ingestor a reçu {} logs", summary.getLogsProcessed());
            }

            @Override
            public void onError(Throwable t) {
                log.warn("Échec de l'envoi gRPC : {}", t.getMessage());
            }

            @Override
            public void onCompleted() {
                // Flux fermé avec succès
            }
        });

        // Envoi du message unique
        try {
            requestObserver.onNext(message);
            requestObserver.onCompleted();
        } catch (Exception e) {
            log.error("Erreur critique lors de l'émission du log : {}", e.getMessage());
            requestObserver.onError(e);
        }
    }
}
