package io.electrica.connector.slack.channel.v1;

import io.electrica.connector.slack.channel.v1.model.SlackChannelV1Action;
import io.electrica.connector.slack.channel.v1.model.SlackChannelV1SendTextPayload;
import io.electrica.connector.spi.ServiceFacade;
import io.electrica.connector.spi.Validations;
import io.electrica.connector.spi.exception.ExceptionCodes;
import io.electrica.connector.spi.exception.IntegrationException;
import io.electrica.connector.test.ElectricaEmulator;
import io.electrica.connector.test.InvocationContext;
import io.electrica.connector.test.MapConnectorProperties;
import io.electrica.connector.test.security.SecuredAuthorizations;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SlackChannelV1SendAttchmentExecutorFactoryTest {

    private static final String TEST_CHANNEL_TOKEN = "" +
            "JiMzedqX8thrLBlg49i9RPNA+d0KSY3nY5Ez2o4QPuwtRycWsMgE4CfiApASwNEwir8IuUJMjYTn+WoXR8mJMBS/Ws2gaGa8udj8c6CD" +
            "B6/0tF7ToB/l7sBadZ2W+sPgVPqC6hc8qIKwCoNUIgsLBs7bULTre4jyhAJPNlEhAYSIshb46cBGhGW9B+xVz2Oe7mJUdQ3vrZfyFdVM" +
            "vL0TAVED5SFNkHq8InEPUUaxcFxbzOpx9O+yU3cJjyEotrfm+E71IJbSJdoGKQGrMouDfki2c4a59/YM0JA3gYKvzTiN5pyFXSuS1OS0" +
            "Bp7AGE8KlMv1/oE0k/PeGCByN4soVA==";

    private static final String ATTACHMENT_MESSAGE = "{\"attachments\":[{\"color\":\"#36a64f\",\"pretext\":" +
            "\"Slack Attachment Test\",\"title\":\"Test Title\",\"text\":\"Optional text that appears within the " +
            "attachment\",\"fields\":[]}]}";

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
                .payload(new SlackChannelV1SendTextPayload().message(ATTACHMENT_MESSAGE))
                .build();
        IntegrationException e = assertThrows(IntegrationException.class, () -> emulator.runIntegration(context));
        assertEquals(ExceptionCodes.VALIDATION, e.getCode());
    }

    @Test
    void testSendText() throws IntegrationException {
        InvocationContext context = InvocationContext.builder(SlackChannelV1Action.SENDATTACHMENT)
                .authorization(SecuredAuthorizations.token(TEST_CHANNEL_TOKEN))
                .payload(new SlackChannelV1SendTextPayload().message(ATTACHMENT_MESSAGE))
                .build();
        Object result = emulator.runIntegration(context);
        assertNull(result);
    }

    @Test
    void testSendTextMissedPayloadThrowException() {
        InvocationContext context = InvocationContext.builder(SlackChannelV1Action.SENDATTACHMENT)
                .authorization(SecuredAuthorizations.token(TEST_CHANNEL_TOKEN))
                .build();

        IntegrationException e = assertThrows(IntegrationException.class, () -> emulator.runIntegration(context));
        assertEquals(ExceptionCodes.VALIDATION, e.getCode());
        assertEquals(ServiceFacade.REQUIRED_PAYLOAD_ERROR_MESSAGE, e.getMessage());
    }

    @Test
    void testSendTextMissedPayloadMessageThrowException() {
        InvocationContext context = InvocationContext.builder(SlackChannelV1Action.SENDATTACHMENT)
                .authorization(SecuredAuthorizations.token(TEST_CHANNEL_TOKEN))
                .payload(new SlackChannelV1SendTextPayload())
                .build();

        IntegrationException e = assertThrows(IntegrationException.class, () -> emulator.runIntegration(context));
        assertEquals(ExceptionCodes.VALIDATION, e.getCode());
        assertEquals(Validations.requiredPayloadFieldErrorMessage("message"), e.getMessage());
    }

    @Test
    void testSendTextMissedAuthorizationThrowException() {
        InvocationContext context = InvocationContext.builder(SlackChannelV1Action.SENDATTACHMENT)
                .payload(new SlackChannelV1SendTextPayload())
                .build();

        IntegrationException e = assertThrows(IntegrationException.class, () -> emulator.runIntegration(context));
        assertEquals(ExceptionCodes.VALIDATION, e.getCode());
        assertEquals(ServiceFacade.REQUIRED_TOKEN_AUTHORIZATION_ERROR_MESSAGE, e.getMessage());
    }
}
