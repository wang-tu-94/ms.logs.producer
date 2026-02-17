package com.myproject.log.aspect;

import com.myproject.log.annotation.Loggable;
import com.myproject.log.client.LogGrpcClient;
import com.myproject.log.proto.LogMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.time.Instant;

@Aspect
@Slf4j
@RequiredArgsConstructor
public class LogAspect {
    private final LogGrpcClient logGrpcClient;

    @Around("@annotation(loggable)")
    public Object logExecution(ProceedingJoinPoint joinPoint, Loggable loggable) throws Throwable {
        long start = System.currentTimeMillis();

        try {
            // Exécution de la méthode métier
            Object result = joinPoint.proceed();

            // Capture après succès
            captureAndSend(joinPoint, loggable, "SUCCESS", System.currentTimeMillis() - start);
            return result;

        } catch (Throwable t) {
            // Capture même en cas d'erreur
            captureAndSend(joinPoint, loggable, "FAILED: " + t.getClass().getSimpleName(), System.currentTimeMillis() - start);
            throw t;
        }
    }

    private void captureAndSend(ProceedingJoinPoint joinPoint, Loggable loggable, String status, long duration) {
        // Extraction des métadonnées de la méthode
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        // Construction du message gRPC
        LogMessage message = LogMessage.newBuilder()
                .setServiceName(className)
                .setLevel(loggable.level())
                .setTimestamp(Instant.now().toString())
                .setContent(String.format("[%s] %s() -> %s in %dms",
                        loggable.category(), methodName, status, duration))
                .build();

        // Délégation de l'envoi au client (non-bloquant)
        logGrpcClient.emit(message);
    }
}
