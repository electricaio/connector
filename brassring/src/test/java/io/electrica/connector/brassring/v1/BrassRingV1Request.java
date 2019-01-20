package io.electrica.connector.brassring.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.electrica.connector.brassring.application.v1.model.BrassRingV1Action;
import io.electrica.connector.brassring.application.v1.model.Status;
import io.electrica.connector.brassring.v1.model.*;
import io.electrica.connector.spi.ServiceFacade;
import io.electrica.connector.spi.exception.ExceptionCodes;
import io.electrica.connector.spi.exception.IntegrationException;
import io.electrica.connector.test.InvocationContext;
import io.electrica.connector.test.security.SecuredAuthorizations;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class BrassRingV1Request extends BrassRingV1BaseTest {

    private static XmlMapper xmlMapper = new XmlMapper();
    private static MockWebServer server = new MockWebServer();

    @BeforeAll
    static void setUp() throws IOException {
        server.start();
        String url = server.url("/testPut").toString();
        init(url);
    }

    @AfterAll
    static void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    void testPut() throws IntegrationException, IOException {
        mockGoodResponse();
        InvocationContext context = InvocationContext.builder(BrassRingV1Action.PUT)
                .authorization(SecuredAuthorizations.token(BRASSRING_TOKEN))
                .payload(createTestRequest())
                .build();
        Object result = emulator.runIntegration(context);
        Envelope envelope = xmlMapper.readValue(result.toString(), Envelope.class);
        assertEquals(200, envelope.getTransactInfo().getStatus().getCode().intValue());
        assertEquals("Success", envelope.getTransactInfo().getStatus().getShortDescription());
        assertEquals("Candidate data transfer was  successful", envelope.getTransactInfo().getStatus()
                .getLongDescription());
    }

    @Test
    void testGetMissedPayloadThrowException() {
        InvocationContext context = InvocationContext.builder(BrassRingV1Action.PUT)
                .authorization(SecuredAuthorizations.token(BRASSRING_TOKEN))
                .build();

        IntegrationException e = assertThrows(IntegrationException.class, () -> emulator.runIntegration(context));
        assertEquals(ExceptionCodes.VALIDATION, e.getCode());
        assertEquals(ServiceFacade.REQUIRED_PAYLOAD_ERROR_MESSAGE, e.getMessage());
    }

    @Test
    void testGetMissedAuthorizationThrowException() {
        InvocationContext context = InvocationContext.builder(BrassRingV1Action.PUT)
                .payload(createTestRequest())
                .build();

        IntegrationException e = assertThrows(IntegrationException.class, () -> emulator.runIntegration(context));
        assertEquals(ExceptionCodes.VALIDATION, e.getCode());
        assertEquals(ServiceFacade.REQUIRED_TOKEN_AUTHORIZATION_ERROR_MESSAGE, e.getMessage());
    }

    @Test
    void unsuccessfulResponseThrowsAnError() throws IOException {
        mockErrorResponse();
        InvocationContext context = InvocationContext.builder(BrassRingV1Action.PUT)
                .authorization(SecuredAuthorizations.token(BRASSRING_TOKEN))
                .payload(createTestRequest())
                .build();

        IntegrationException e = assertThrows(IntegrationException.class, () -> emulator.runIntegration(context));
        Envelope result = xmlMapper.readValue(e.getMessage(), Envelope.class);
        assertNotEquals(200, result.getTransactInfo().getStatus().getCode());
        assertEquals("Test Short Desc", result.getTransactInfo().getStatus().getShortDescription());
        assertEquals("Test Long Desc", result.getTransactInfo().getStatus().getLongDescription());
    }

    private void mockGoodResponse() throws JsonProcessingException {
        server.enqueue(
                new MockResponse()
                        .setBody(createGoodResponse())
                        .setResponseCode(200)
        );
    }

    private void mockErrorResponse() throws JsonProcessingException {
        server.enqueue(
                new MockResponse()
                        .setBody(createBadResponse())
                        .setResponseCode(404)
        );
    }

    private Envelope createTestRequest() {
        Sender sender = new Sender("Test", "Token");
        String payload = "<?xml version=\"1.0\"?>" +
                "<CANDIDATE>" +
                "<CANDIDATEID>984577</CANDIDATEID>" +
                "<REQUISITIONNUMBER/>" +
                "<BRREQNUMBER>100BR</BRREQNUMBER>" +
                "<JOBCODE>09845145447</JOBCODE>" +
                "<STATUS>03-Offer Accepted</STATUS>" +
                "<CANDIDATEFIRSTNAME></CANDIDATEFIRSTNAME>" +
                "<CANDIDATELASTNAME></CANDIDATELASTNAME>" +
                "<DATEOFBIRTH>1977-02-15</DATEOFBIRTH>" +
                "<BIRTHPLACE>Boston</BIRTHPLACE>" +
                "<COUNTRYOFBIRTH>USA</COUNTRYOFBIRTH>" +
                "<GENDER>M</GENDER>" +
                "<MARITALSTATUS>Single</MARITALSTATUS>" +
                "<NATIONALITY>American</NATIONALITY>" +
                "<LANGUAGENAME>EN</LANGUAGENAME>" +
                "</CANDIDATE>";
        PacketInfo packetInfo = new PacketInfo("data", "1", "SET", "MANIFEST_NAME", null);
        Packet packet = new Packet(packetInfo, payload);
        TransactInfo transactInfo = new TransactInfo("data", "HSCAND636096", "2/28/2011 5:34:34 PM", null);
        return createEnvelop(sender, null, transactInfo, packet);
    }

    private String createGoodResponse() throws JsonProcessingException {
        Sender sender = new Sender("Test", "Token");
        Status status = new Status();
        status.setCode(200);
        status.setLongDescription("Candidate data transfer was  successful");
        status.setShortDescription("Success");
        PacketInfo packetInfo = new PacketInfo("data", "1", "SET", "MANIFEST_NAME", status);
        Packet packet = new Packet(packetInfo, null);
        TransactInfo transactInfo = new TransactInfo("data", "HSCAND636096", "2/28/2011 5:34:34 PM", status);
        return xmlMapper.writeValueAsString(createEnvelop(sender, null, transactInfo, packet));
    }

    private String createBadResponse() throws JsonProcessingException {
        Sender sender = new Sender("Test", "Token");
        Status status = new Status();
        status.setCode(405);
        status.setLongDescription("Test Long Desc");
        status.setShortDescription("Test Short Desc");
        PacketInfo packetInfo = new PacketInfo("data", "1", "SET", "MANIFEST_NAME", status);
        Packet packet = new Packet(packetInfo, null);
        TransactInfo transactInfo = new TransactInfo("data", "HSCAND636096", "2/28/2011 5:34:34 PM", status);
        return xmlMapper.writeValueAsString(createEnvelop(sender, null, transactInfo, packet));
    }

    private Envelope createEnvelop(Sender sender, Recipient recipient, TransactInfo transactInfo, Packet packet) {
        Envelope envelope = new Envelope();
        envelope.setVersion("01.00");
        envelope.setSender(sender);
        envelope.setRecipient(recipient);
        envelope.setTransactInfo(transactInfo);
        envelope.setPackets(new Packet[]{packet});
        return envelope;
    }
}
