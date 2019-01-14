package io.electrica.connector.slack.channel.v1;

import io.electrica.connector.slack.channel.v1.model.Attachment;
import io.electrica.connector.slack.channel.v1.model.Field;
import io.electrica.connector.slack.channel.v1.model.SlackChannelV1Action;
import io.electrica.connector.slack.channel.v1.model.SlackChannelV1SendAttachmentPayload;
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
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
class SlackChannelV1SendAttchmentExecutorFactoryTest {

    private static final String TEST_CHANNEL_TOKEN = "" +
            "JiMzedqX8thrLBlg49i9RPNA+d0KSY3nY5Ez2o4QPuwtRycWsMgE4CfiApASwNEwir8IuUJMjYTn+WoXR8mJMBS/Ws2gaGa8udj8c6CD" +
            "B6/0tF7ToB/l7sBadZ2W+sPgVPqC6hc8qIKwCoNUIgsLBs7bULTre4jyhAJPNlEhAYSIshb46cBGhGW9B+xVz2Oe7mJUdQ3vrZfyFdVM" +
            "vL0TAVED5SFNkHq8InEPUUaxcFxbzOpx9O+yU3cJjyEotrfm+E71IJbSJdoGKQGrMouDfki2c4a59/YM0JA3gYKvzTiN5pyFXSuS1OS0" +
            "Bp7AGE8KlMv1/oE0k/PeGCByN4soVA==";

    private List<Attachment> attachments;

    @BeforeAll
    void setup() {
        attachments = createAttachmentList();
    }

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
                .payload(new SlackChannelV1SendAttachmentPayload().attachments(attachments))
                .build();
        IntegrationException e = assertThrows(IntegrationException.class, () -> emulator.runIntegration(context));
        assertEquals(ExceptionCodes.VALIDATION, e.getCode());
    }

    @Test
    void testSendAttachmentMessage() throws IntegrationException {
        InvocationContext context = InvocationContext.builder(SlackChannelV1Action.SENDATTACHMENT)
                .authorization(SecuredAuthorizations.token(TEST_CHANNEL_TOKEN))
                .payload(new SlackChannelV1SendAttachmentPayload().attachments(attachments))
                .build();
        Object result = emulator.runIntegration(context);
        assertNull(result);
    }

    @Test
    void testSendAttachmentMissedPayloadThrowException() {
        InvocationContext context = InvocationContext.builder(SlackChannelV1Action.SENDATTACHMENT)
                .authorization(SecuredAuthorizations.token(TEST_CHANNEL_TOKEN))
                .build();

        IntegrationException e = assertThrows(IntegrationException.class, () -> emulator.runIntegration(context));
        assertEquals(ExceptionCodes.VALIDATION, e.getCode());
        assertEquals(ServiceFacade.REQUIRED_PAYLOAD_ERROR_MESSAGE, e.getMessage());
    }

    @Test
    void testSendAttachmentMissedPayloadMessageThrowException() {
        InvocationContext context = InvocationContext.builder(SlackChannelV1Action.SENDATTACHMENT)
                .authorization(SecuredAuthorizations.token(TEST_CHANNEL_TOKEN))
                .payload(new SlackChannelV1SendAttachmentPayload())
                .build();

        IntegrationException e = assertThrows(IntegrationException.class, () -> emulator.runIntegration(context));
        assertEquals(ExceptionCodes.VALIDATION, e.getCode());
        assertEquals(Validations.requiredPayloadFieldErrorMessage("attachments"), e.getMessage());
    }

    @Test
    void testSendAttachmentMissedAuthorizationThrowException() {
        InvocationContext context = InvocationContext.builder(SlackChannelV1Action.SENDATTACHMENT)
                .payload(new SlackChannelV1SendAttachmentPayload())
                .build();

        IntegrationException e = assertThrows(IntegrationException.class, () -> emulator.runIntegration(context));
        assertEquals(ExceptionCodes.VALIDATION, e.getCode());
        assertEquals(ServiceFacade.REQUIRED_TOKEN_AUTHORIZATION_ERROR_MESSAGE, e.getMessage());
    }

    @Test
    void testSendMoreThan20AttachmentsInMessageThrowsException() throws IntegrationException {
        List<Attachment> attachmentsList = new ArrayList<>();
        for (int i = 0; i < 21; i++) {
            attachmentsList.add(createAttachment());
        }
        InvocationContext context = InvocationContext.builder(SlackChannelV1Action.SENDATTACHMENT)
                .authorization(SecuredAuthorizations.token(TEST_CHANNEL_TOKEN))
                .payload(new SlackChannelV1SendAttachmentPayload().attachments(attachmentsList))
                .build();
        IntegrationException e = assertThrows(IntegrationException.class, () -> emulator.runIntegration(context));
        assertEquals(ExceptionCodes.VALIDATION, e.getCode());
    }

    private List<Attachment> createAttachmentList() {
        Attachment attachment = createAttachment();
        List<Attachment> attachmentList = new ArrayList<>();
        attachmentList.add(attachment);
        return attachmentList;
    }

    private Attachment createAttachment() {
        Attachment attachment = new Attachment();
        attachment.setColor("#36a64f");
        attachment.setPretext("Slack Attachment Test");
        attachment.setAuthorName("Electrica");
        attachment.setAuthorLink("https://www.electrica.io/");
        attachment.setTitle("Test Title");
        attachment.setTitleLink("https://www.electrica.io/");
        attachment.setText("Optional text that appears within the attachment");
        Field field = new Field();
        field.setShort(false);
        field.setTitle("Priority");
        field.setValue("High");
        attachment.setFields(Collections.singletonList(field));
        attachment.setFooter("Slack Message Footer");
        attachment.setTs(new Date().getTime() / 1000);
        return attachment;
    }
}
