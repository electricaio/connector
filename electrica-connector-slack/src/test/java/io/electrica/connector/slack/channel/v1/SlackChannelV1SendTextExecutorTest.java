package io.electrica.connector.slack.channel.v1;

import io.electrica.connector.slack.channel.v1.model.SlackChannelV1Action;
import io.electrica.connector.slack.channel.v1.model.SlackChannelV1SendTextPayload;
import io.electrica.connector.spi.ServiceFacade;
import io.electrica.connector.spi.Validations;
import io.electrica.connector.spi.exception.ExceptionCodes;
import io.electrica.connector.spi.exception.IntegrationException;
import io.electrica.connector.test.InvocationContext;
import io.electrica.connector.test.security.SecuredAuthorizations;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SlackChannelV1SendTextExecutorTest extends SlackChannelV1BaseTest {

    @Test
    void testSendText() throws IntegrationException {
        InvocationContext context = InvocationContext.builder(SlackChannelV1Action.SENDTEXT)
                .authorization(SecuredAuthorizations.token(TEST_CHANNEL_TOKEN))
                .payload(new SlackChannelV1SendTextPayload().message("Integration test message"))
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
                .payload(new SlackChannelV1SendTextPayload())
                .build();

        IntegrationException e = assertThrows(IntegrationException.class, () -> emulator.runIntegration(context));
        assertEquals(ExceptionCodes.VALIDATION, e.getCode());
        assertEquals(Validations.requiredPayloadFieldErrorMessage("message"), e.getMessage());
    }

    @Test
    void testSendTextMissedAuthorizationThrowException() {
        InvocationContext context = InvocationContext.builder(SlackChannelV1Action.SENDTEXT)
                .payload(new SlackChannelV1SendTextPayload().message("Integration test message"))
                .build();

        IntegrationException e = assertThrows(IntegrationException.class, () -> emulator.runIntegration(context));
        assertEquals(ExceptionCodes.VALIDATION, e.getCode());
        assertEquals(ServiceFacade.REQUIRED_TOKEN_AUTHORIZATION_ERROR_MESSAGE, e.getMessage());
    }
}
