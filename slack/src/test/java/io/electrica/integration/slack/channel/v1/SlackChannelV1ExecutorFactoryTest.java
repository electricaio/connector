package io.electrica.integration.slack.channel.v1;

import io.electrica.integration.slack.channel.v1.model.SlackChannelV1Action;
import io.electrica.integration.slack.channel.v1.model.SlackChannelV2SendTextPayload;
import io.electrica.integration.spi.ServiceFacade;
import io.electrica.integration.spi.Validations;
import io.electrica.integration.spi.exception.ExceptionCodes;
import io.electrica.integration.spi.exception.IntegrationException;
import io.electrica.integration.test.ElectricaEmulator;
import io.electrica.integration.test.InvocationContext;
import io.electrica.integration.test.MapConnectorProperties;
import io.electrica.integration.test.security.SecuredAuthorizations;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SlackChannelV1ExecutorFactoryTest {

    private static final String TEST_CHANNEL_TOKEN = "" +
            "JiMzedqX8thrLBlg49i9RPNA+d0KSY3nY5Ez2o4QPuwtRycWsMgE4CfiApASwNEwir8IuUJMjYTn+WoXR8mJMBS/Ws2gaGa8udj8c6CD" +
            "B6/0tF7ToB/l7sBadZ2W+sPgVPqC6hc8qIKwCoNUIgsLBs7bULTre4jyhAJPNlEhAYSIshb46cBGhGW9B+xVz2Oe7mJUdQ3vrZfyFdVM" +
            "vL0TAVED5SFNkHq8InEPUUaxcFxbzOpx9O+yU3cJjyEotrfm+E71IJbSJdoGKQGrMouDfki2c4a59/YM0JA3gYKvzTiN5pyFXSuS1OS0" +
            "Bp7AGE8KlMv1/oE0k/PeGCByN4soVA==";

    private static ElectricaEmulator emulator;

    @BeforeAll
    static void setUp() {
        MapConnectorProperties connectorProperties = MapConnectorProperties.builder()
                .addInteger(SlackChannelV1ExecutorFactory.MAX_IDLE_CONNECTIONS_PROPERTY, 1)
                .addInteger(SlackChannelV1ExecutorFactory.KEEP_ALIVE_DURATION_MIN_PROPERTY, 1)
                .build();

        emulator = ElectricaEmulator.builder(SlackChannelV1ExecutorFactory.class)
                .connectorProperties(connectorProperties)
                .build();
    }

    @Test
    void testUnsupportedActionThrowException() {
        InvocationContext context = InvocationContext.builder("unsupported-action")
                .authorization(SecuredAuthorizations.token(TEST_CHANNEL_TOKEN))
                .payload(new SlackChannelV2SendTextPayload().message("Integration test message"))
                .build();
        IntegrationException e = assertThrows(IntegrationException.class, () -> emulator.runIntegration(context));
        assertEquals(ExceptionCodes.VALIDATION, e.getCode());
    }

    @Test
    void testSendText() throws IntegrationException {
        InvocationContext context = InvocationContext.builder(SlackChannelV1Action.SENDTEXT)
                .authorization(SecuredAuthorizations.token(TEST_CHANNEL_TOKEN))
                .payload(new SlackChannelV2SendTextPayload().message("Integration test message"))
                .build();
        Object result = emulator.runIntegration(context);
        assertNull(result);
    }

    @Test
    void testSendTextMissedPayloadThrowException() {
        InvocationContext context = InvocationContext.builder(SlackChannelV1Action.SENDTEXT)
                .authorization(SecuredAuthorizations.token(TEST_CHANNEL_TOKEN))
                .build();

        IntegrationException e = assertThrows(IntegrationException.class, () -> emulator.runIntegration(context));
        assertEquals(ExceptionCodes.VALIDATION, e.getCode());
        assertEquals(ServiceFacade.REQUIRED_PAYLOAD_ERROR_MESSAGE, e.getMessage());
    }

    @Test
    void testSendTextMissedPayloadMessageThrowException() {
        InvocationContext context = InvocationContext.builder(SlackChannelV1Action.SENDTEXT)
                .authorization(SecuredAuthorizations.token(TEST_CHANNEL_TOKEN))
                .payload(new SlackChannelV2SendTextPayload())
                .build();

        IntegrationException e = assertThrows(IntegrationException.class, () -> emulator.runIntegration(context));
        assertEquals(ExceptionCodes.VALIDATION, e.getCode());
        assertEquals(Validations.requiredPayloadFieldErrorMessage("message"), e.getMessage());
    }

    @Test
    void testSendTextMissedAuthorizationThrowException() {
        InvocationContext context = InvocationContext.builder(SlackChannelV1Action.SENDTEXT)
                .payload(new SlackChannelV2SendTextPayload().message("Integration test message"))
                .build();

        IntegrationException e = assertThrows(IntegrationException.class, () -> emulator.runIntegration(context));
        assertEquals(ExceptionCodes.VALIDATION, e.getCode());
        assertEquals(ServiceFacade.REQUIRED_TOKEN_AUTHORIZATION_ERROR_MESSAGE, e.getMessage());
    }

}
