package io.electrica.connector.slack.channel.v1;

import io.electrica.connector.slack.channel.v1.model.*;
import io.electrica.connector.spi.ServiceFacade;
import io.electrica.connector.spi.exception.ExceptionCodes;
import io.electrica.connector.spi.exception.IntegrationException;
import io.electrica.connector.test.InvocationContext;
import io.electrica.connector.test.security.SecuredAuthorizations;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static io.electrica.connector.slack.channel.v1.SlackChannelV1SendAttachmentExecutor.AT_LEAST_ONE_ATTACHMENT_PRESENT;
import static io.electrica.connector.slack.channel.v1.SlackChannelV1SendAttachmentExecutor.TOO_MANY_ATTACHMENTS;
import static org.junit.jupiter.api.Assertions.*;

class SlackChannelV1SendAttachmentExecutorTest extends SlackChannelV1BaseTest {

    @Test
    void testSendAttachmentMessage() throws IntegrationException {
        InvocationContext context = InvocationContext.builder(SlackChannelV1Action.SENDATTACHMENT)
                .authorization(SecuredAuthorizations.token(TEST_CHANNEL_TOKEN))
                .payload(new SlackChannelV1SendAttachmentPayload().attachments(createAttachmentList()))
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
        assertEquals(AT_LEAST_ONE_ATTACHMENT_PRESENT, e.getMessage());
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
    void testSendMoreThan20AttachmentsInMessageThrowsException() {
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
        assertEquals(TOO_MANY_ATTACHMENTS, e.getMessage());
    }

    private List<Attachment> createAttachmentList() {
        return Collections.singletonList(createAttachment());
    }

    private Attachment createAttachment() {
        Attachment attachment = new Attachment();
        attachment.setFallback("Fallback String");
        attachment.setColor("#36a64f");
        attachment.setPretext("Slack Attachment Test");
        attachment.setAuthorName("Electrica");
        attachment.setAuthorLink("https://www.electrica.io/");
        attachment.setTitle("Test Title");
        attachment.setTitleLink("https://www.electrica.io/");
        attachment.setText("Optional text that appears within the attachment");
        Action action = new Action();
        action.setName("Test Action");
        action.setText("Button text");
        action.setType(Action.TypeEnum.BUTTON);
        action.setUrl("https://www.electrica.io/");
        action.setConfirm("Are you sure?");
        Field field = new Field();
        field.setShort(false);
        field.setTitle("Priority");
        field.setValue("High");
        List<Action> actions = new ArrayList<>();
        actions.add(action);
        List<Field> fields = new ArrayList<>();
        fields.add(field);
        attachment.setActions(actions);
        attachment.setFields(fields);
        attachment.setFooter("Slack Message Footer");
        attachment.setTs(new Date().getTime() / 1000);
        return attachment;
    }
}
