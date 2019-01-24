package io.electrica.connector.hackerrank.v3.tests.v1;

import io.electrica.connector.hackerrank.v3.tests.v1.model.HackerRankV3TestsAction;
import io.electrica.connector.hackerrank.v3.tests.v1.model.HackerRankV3TestsShowPayload;
import io.electrica.connector.spi.exception.ExceptionCodes;
import io.electrica.connector.spi.exception.IntegrationException;
import io.electrica.connector.test.ElectricaEmulator;
import io.electrica.connector.test.InvocationContext;
import io.electrica.connector.test.security.SecuredAuthorizations;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShowTestV1ExecutorTest {

    private static final String HR_ACCESS_TOKEN = "eSJfx9rxQeC7OO5cdpjkEglihlt2d+0VeBC/7ymKLMl7UC0ypUZpUS" +
            "cQpefLbCHkMWWMeE+pUUJiIAvUTrZpdKE6Q3550FtUoVzAyZL0ELOWo09Wm/6TcIgMI8Ea3NVwVs/AI01fgRctKCm/w6+QbGY8BHIM" +
            "O3a38aDasClpBzFNwNYjgey914a0mLNKtJut23kMu9X8Yhy3hEiOp+uSFo3ygXSvAjEKUWlhY8lqnNRvzXjyf8Fc4mMO0Yn8" +
            "9AWUTK0AUYiBG8A6gBMrnMHgDAMC/5IpRl0p55OQ/jjftLVPah2kJesKMdB0vxKOEtm7sBsfgujpV/TVuos4AGeOrw==";

    private ElectricaEmulator emulator = ElectricaEmulator.of(HackerRankTestsV1ExecutorFactory.class);

    @Test
    void unsupportedActionTest() {
        InvocationContext context = InvocationContext.builder("unsupported-action")
                .build();

        IntegrationException e = assertThrows(IntegrationException.class, () -> emulator.runIntegration(context));
        assertEquals(ExceptionCodes.VALIDATION, e.getCode());
    }

    @Test
    void individualTestIsReturnedTest() throws IntegrationException {
        InvocationContext context = InvocationContext.builder(HackerRankV3TestsAction.TESTSSHOW)
                .authorization(SecuredAuthorizations.token(HR_ACCESS_TOKEN))
                .payload(new HackerRankV3TestsShowPayload().id(359653))
                .build();

        Object result = emulator.runIntegration(context);
        assertNotNull(result);
    }

    @Test
    void errorThrownForUnknownTest() {
        InvocationContext context = InvocationContext.builder(HackerRankV3TestsAction.TESTSSHOW)
                .authorization(SecuredAuthorizations.token(HR_ACCESS_TOKEN))
                .payload(new HackerRankV3TestsShowPayload().id(-1))
                .build();

        assertThrows(IntegrationException.class, () -> emulator.runIntegration(context));
    }
}
