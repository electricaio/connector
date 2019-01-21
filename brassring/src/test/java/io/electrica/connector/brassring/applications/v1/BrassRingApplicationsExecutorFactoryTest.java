package io.electrica.connector.brassring.applications.v1;

import io.electrica.connector.spi.exception.ExceptionCodes;
import io.electrica.connector.spi.exception.IntegrationException;
import io.electrica.connector.test.InvocationContext;
import io.electrica.connector.test.security.SecuredAuthorizations;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BrassRingApplicationsExecutorFactoryTest extends BrassRingV1BaseTest {

    @BeforeAll
    static void setUp() {
        init("");
    }

    @Test
    void testUnsupportedActionThrowException() {
        InvocationContext context = InvocationContext.builder("unsupported-action")
                .authorization(SecuredAuthorizations.token(BRASSRING_TOKEN))
                .build();
        IntegrationException e = assertThrows(IntegrationException.class, () -> emulator.runIntegration(context));
        assertEquals(ExceptionCodes.VALIDATION, e.getCode());
    }
}
