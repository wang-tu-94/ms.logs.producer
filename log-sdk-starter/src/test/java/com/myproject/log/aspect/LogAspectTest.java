package com.myproject.log.aspect;

import com.myproject.log.annotation.Loggable;
import com.myproject.log.client.LogGrpcClient;
import com.myproject.log.proto.LogMessage;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogAspectTest {
    @Mock
    private LogGrpcClient logGrpcClient;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    @InjectMocks
    private LogAspect logAspect;

    @Test
    void shouldInterceptAndSendLog() throws Throwable {
        // GIVEN
        Loggable loggable = mock(Loggable.class);
        when(loggable.level()).thenReturn("INFO");
        when(loggable.category()).thenReturn("TEST");

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getName()).thenReturn("myMethod");
        when(methodSignature.getDeclaringType()).thenReturn(Object.class);
        when(joinPoint.proceed()).thenReturn("result");

        // WHEN
        Object result = logAspect.logExecution(joinPoint, loggable);

        // THEN
        assertThat(result).isEqualTo("result");

        // On vérifie qu'un message a bien été envoyé au client gRPC
        ArgumentCaptor<LogMessage> captor = ArgumentCaptor.forClass(LogMessage.class);
        verify(logGrpcClient).emit(captor.capture());

        LogMessage sentMessage = captor.getValue();
        assertThat(sentMessage.getContent()).contains("myMethod");
        assertThat(sentMessage.getContent()).contains("SUCCESS");
    }
}