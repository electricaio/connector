package io.electrica.connector.hackerrank.v3.tests.v1;

import io.electrica.connector.hackerrank.v3.tests.v1.model.HackerRankV3TestsAction;
import io.electrica.connector.hackerrank.v3.tests.v1.model.LimitOffset;
import io.electrica.connector.spi.exception.ExceptionCodes;
import io.electrica.connector.spi.exception.IntegrationException;
import io.electrica.connector.test.ElectricaEmulator;
import io.electrica.connector.test.InvocationContext;
import io.electrica.connector.test.security.SecuredAuthorizations;
import org.junit.jupiter.api.Test;

import static io.electrica.connector.hackerrank.v3.TestConstants.HR_ACCESS_TOKEN;
import static org.junit.jupiter.api.Assertions.*;

class TestsIndexV1ExecutorTest {

    private ElectricaEmulator emulator = ElectricaEmulator.of(HackerRankTestsV1ExecutorFactory.class);

    @Test
    void unsupportedActionTest() {
        InvocationContext context = InvocationContext.builder("unsupported-action")
                .build();

        IntegrationException e = assertThrows(IntegrationException.class, () -> emulator.runIntegration(context));
        assertEquals(ExceptionCodes.VALIDATION, e.getCode());
    }

    @Test
    void returnsAllActiveTests() throws IntegrationException {
        InvocationContext context = InvocationContext.builder(HackerRankV3TestsAction.TESTSINDEX)
                .authorization(SecuredAuthorizations.token(HR_ACCESS_TOKEN))
                .payload(new LimitOffset().limit(10).offset(0))
                .build();

        Object result = emulator.runIntegration(context);
        assertNotNull(result);
    }
}
