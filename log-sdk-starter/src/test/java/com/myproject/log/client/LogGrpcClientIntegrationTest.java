package com.myproject.log.client;

import com.myproject.log.proto.LogMessage;
import com.myproject.log.proto.LogServiceGrpc;
import com.myproject.log.proto.LogSummary;
import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class LogGrpcClientIntegrationTest {
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    private LogServiceGrpc.LogServiceStub stub;
    private final AtomicReference<LogMessage> receivedMessage = new AtomicReference<>();
    private CountDownLatch latch;

    @BeforeEach
    void setup() throws Exception {
        latch = new CountDownLatch(1); // On attend 1 événement

        // On implémente un VRAI service de test au lieu d'un Mockito instable
        LogServiceGrpc.LogServiceImplBase serviceImpl = new LogServiceGrpc.LogServiceImplBase() {
            @Override
            public StreamObserver<LogMessage> sendLogs(StreamObserver<LogSummary> responseObserver) {
                return new StreamObserver<>() {
                    @Override
                    public void onNext(LogMessage value) {
                        receivedMessage.set(value); // On capture le message reçu
                    }

                    @Override
                    public void onError(Throwable t) {}

                    @Override
                    public void onCompleted() {
                        responseObserver.onNext(LogSummary.newBuilder().setLogsProcessed(1).build());
                        responseObserver.onCompleted();
                        latch.countDown(); // ON LIBÈRE LE TEST ICI
                    }
                };
            }
        };

        String serverName = InProcessServerBuilder.generateName();
        grpcCleanup.register(InProcessServerBuilder.forName(serverName).directExecutor().addService(serviceImpl).build().start());
        ManagedChannel channel = grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build());
        stub = LogServiceGrpc.newStub(channel);
    }

    @Test
    void emit_ShouldSendToGrpcServerSynchronously() throws InterruptedException {
        // GIVEN
        LogGrpcClient client = new LogGrpcClient(stub);
        LogMessage message = LogMessage.newBuilder().setContent("Test Log").build();

        // WHEN
        client.emit(message);

        // THEN
        // On attend que le serveur dise "j'ai fini" (max 5 secondes, au cas où)
        boolean completed = latch.await(5, TimeUnit.SECONDS);

        assertThat(completed).isTrue(); // Si c'est false, c'est que le serveur n'a jamais reçu le onCompleted
        assertThat(receivedMessage.get().getContent()).isEqualTo("Test Log");
    }
}