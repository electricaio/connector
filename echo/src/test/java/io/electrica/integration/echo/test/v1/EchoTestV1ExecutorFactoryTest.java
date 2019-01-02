package io.electrica.integration.echo.test.v1;

import io.electrica.integration.echo.test.v1.model.*;
import io.electrica.integration.spi.exception.ExceptionCodes;
import io.electrica.integration.spi.exception.IntegrationException;
import io.electrica.integration.test.ElectricaEmulator;
import io.electrica.integration.test.InvocationContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EchoTestV1ExecutorFactoryTest {

    private ElectricaEmulator emulator = ElectricaEmulator.of(EchoTestV1ExecutorFactory.class);

    @Test
    void unsupportedActionTest() {
        InvocationContext context = InvocationContext.builder("unsupported-action")
                .parameters(new EchoTestV1PingParameters().throwException(false))
                .build();

        IntegrationException e = assertThrows(IntegrationException.class, () -> emulator.runIntegration(context));
        assertEquals(ExceptionCodes.VALIDATION, e.getCode());
    }

    @Test
    void pingTest() throws IntegrationException {
        InvocationContext context = InvocationContext.builder(EchoTestV1Action.PING)
                .parameters(new EchoTestV1PingParameters().throwException(false))
                .build();

        Object result = emulator.runIntegration(context);
        assertNull(result);
    }

    @Test
    void pingTestThrowException() {
        InvocationContext context = InvocationContext.builder(EchoTestV1Action.PING)
                .parameters(new EchoTestV1PingParameters().throwException(true))
                .build();

        IntegrationException e = assertThrows(IntegrationException.class, () -> emulator.runIntegration(context));
        assertEquals(ExceptionCodes.GENERIC, e.getCode());
        assertEquals(EchoTestV1ExecutorFactory.REQUESTED_ERROR_MESSAGE, e.getMessage());
    }

    @Test
    void sendTest() throws IntegrationException {
        String message = "test-massage";
        InvocationContext context = InvocationContext.builder(EchoTestV1Action.SEND)
                .parameters(new EchoTestV1SendParameters().throwException(false))
                .payload(new EchoTestV1SendPayload().message(message))
                .build();

        EchoTestV1SendResult result = emulator.runIntegration(EchoTestV1SendResult.class, context);
        assertNotNull(result);
        assertEquals(message, result.getMessage());
    }

    @Test
    void sendTestThrowException() {
        String message = "test-massage";
        InvocationContext context = InvocationContext.builder(EchoTestV1Action.SEND)
                .parameters(new EchoTestV1SendParameters().throwException(true))
                .payload(new EchoTestV1SendPayload().message(message))
                .build();

        IntegrationException e = assertThrows(IntegrationException.class, () -> emulator.runIntegration(context));
        assertEquals(ExceptionCodes.GENERIC, e.getCode());
        assertEquals(EchoTestV1ExecutorFactory.REQUESTED_ERROR_MESSAGE, e.getMessage());
    }

}
