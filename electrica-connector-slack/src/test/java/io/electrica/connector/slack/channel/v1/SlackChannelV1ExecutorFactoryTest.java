package io.electrica.connector.slack.channel.v1;

import io.electrica.connector.slack.channel.v1.model.SlackChannelV1SendTextPayload;
import io.electrica.connector.spi.exception.ExceptionCodes;
import io.electrica.connector.spi.exception.IntegrationException;
import io.electrica.connector.test.InvocationContext;
import io.electrica.connector.test.security.SecuredAuthorizations;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SlackChannelV1ExecutorFactoryTest extends SlackChannelV1BaseTest {

    @Test
    void testUnsupportedActionThrowException() {
        InvocationContext context = InvocationContext.builder("unsupported-action")
                .authorization(SecuredAuthorizations.token(TEST_CHANNEL_TOKEN))
                .payload(new SlackChannelV1SendTextPayload().message("Integration test message"))
                .build();
        IntegrationException e = assertThrows(IntegrationException.class, () -> emulator.runIntegration(context));
        assertEquals(ExceptionCodes.VALIDATION, e.getCode());
    }
}
