package io.electrica.connector.brassring.application.v1;

import io.electrica.connector.brassring.application.v1.model.BrassRingApplicationAction;
import io.electrica.connector.brassring.application.v1.model.BrassRingApplicationPayload;
import io.electrica.connector.brassring.application.v1.model.FormInput;
import io.electrica.connector.spi.exception.IntegrationException;
import io.electrica.connector.spi.impl.MapConnectorProperties;
import io.electrica.connector.spi.impl.MapObjectProperties;
import io.electrica.connector.test.ElectricaEmulator;
import io.electrica.connector.test.InvocationContext;
import io.electrica.connector.test.security.SecuredAuthorization;
import io.electrica.connector.test.security.SecuredAuthorizations;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationUpdateExecutorTest {

    private static final MockResponse SUCCESS_RESPONSE = new MockResponse()
            .setResponseCode(200)
            .setBody("" +
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<Envelope version=\"01.00\">\n" +
                    "\t<Sender>\n" +
                    "\t\t<Id>BrassRing will provide</Id>\n" +
                    "\t\t<Credential>BrassRing will provide</Credential>\n" +
                    "\t</Sender>\n" +
                    "\t<Packet>\n" +
                    "\t\t<PacketInfo packetType=\"response\">\n" +
                    "\t\t\t<PacketId>1</PacketId>\n" +
                    "\t\t\t<Action>SET</Action>\n" +
                    "\t\t\t<Manifest>MANIFEST_NAME</Manifest>\n" +
                    "\t\t\t<Status>\n" +
                    "\t\t\t\t<Code>200</Code>\n" +
                    "\t\t\t\t<ShortDescription>Candidate data was exported successfully</ShortDescription>\n" +
                    "\t\t\t\t<LongDescription>Candidate data was exported successfully</LongDescription>\n" +
                    "\t\t\t</Status>\n" +
                    "\t\t</PacketInfo>\n" +
                    "\t\t<Payload/>\n" +
                    "\t</Packet>\n" +
                    "\t<Recipient>\n" +
                    "\t\t<Id/>\n" +
                    "\t</Recipient>\n" +
                    "\t<TransactInfo transactType=\"response\">\n" +
                    "\t\t<TransactId>11082520</TransactId>\n" +
                    "\t\t<TimeStamp>11/23/2009 08:54:28 AM PST</TimeStamp>\n" +
                    "\t\t<Status>\n" +
                    "\t\t\t<Code>200</Code>\n" +
                    "\t\t\t<ShortDescription>Candidate data was exported successfully</ShortDescription>\n" +
                    "\t\t\t<LongDescription>Candidate data was exported successfully</LongDescription>\n" +
                    "\t\t</Status>\n" +
                    "\t</TransactInfo>\n" +
                    "</Envelope>\n");
    private static final MockResponse FAILURE_RESPONSE = new MockResponse()
            .setResponseCode(405)
            .setBody("" +
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<Envelope version=\"01.00\">\n" +
                    "\t<Sender>\n" +
                    "\t\t<Id>BrassRing will provide</Id>\n" +
                    "\t\t<Credential>BrassRing will provide</Credential>\n" +
                    "\t</Sender>\n" +
                    "\t<Packet>\n" +
                    "\t\t<PacketInfo packetType=\"response\">\n" +
                    "\t\t\t<PacketId>1</PacketId>\n" +
                    "\t\t\t<Action>SET</Action>\n" +
                    "\t\t\t<Manifest>MANIFEST_NAME</Manifest>\n" +
                    "\t\t\t<Status>\n" +
                    "\t\t\t\t<Code>405</Code>\n" +
                    "\t\t\t\t<ShortDescription>Background Request Submission Not Successful</ShortDescription>\n" +
                    "\t\t\t\t<LongDescription>Missing Required: Applicant Region</LongDescription>\n" +
                    "\t\t\t</Status>\n" +
                    "\t\t</PacketInfo>\n" +
                    "\t\t<Payload/>\n" +
                    "\t</Packet>\n" +
                    "\t<Recipient>\n" +
                    "\t\t<Id/>\n" +
                    "\t</Recipient>\n" +
                    "\t<TransactInfo transactType=\"response\">\n" +
                    "\t\t<TransactId/>\n" +
                    "\t\t<TimeStamp>11/23/2009 03:54:19 PM PST</TimeStamp>\n" +
                    "\t\t<Status>\n" +
                    "\t\t\t<Code>405</Code>\n" +
                    "\t\t\t<ShortDescription>Background Request Submission Not Successful</ShortDescription>\n" +
                    "\t\t\t<LongDescription>Missing Required: Applicant Region</LongDescription>\n" +
                    "\t\t</Status>\n" +
                    "\t</TransactInfo>\n" +
                    "</Envelope>\n");
    private static final String MANIFEST = "TEST_MANIFEST";
    private static final SecuredAuthorization AUTHORIZATION = SecuredAuthorizations.developerIbm("test", "12345");

    private static MockWebServer server = new MockWebServer();
    private static ElectricaEmulator electricaEmulator;

    @BeforeAll
    static void setUp() {
        HttpUrl url = server.url("/test");
        electricaEmulator = ElectricaEmulator.builder(ApplicationExecutorFactory.class)
                .connectorProperties(MapConnectorProperties.builder()
                        .addString(ApplicationExecutorFactory.API_URL_PROPERTY, url.toString())
                        .build()
                )
                .build();
    }

    @AfterAll
    static void tearDown() throws IOException {
        server.close();
    }

    @Test
    void testSuccessful() throws Exception {
        server.enqueue(SUCCESS_RESPONSE);

        invokeUpdate();

        RecordedRequest request = server.takeRequest(10, TimeUnit.SECONDS);
        String contentType = request.getHeader("Content-Type");
        String requestString = request.getBody().readByteString().string(Charset.defaultCharset());
        Pattern requestPattern = Pattern.compile("" +
                "<Envelope version=\"01.00\">" +
                "<Sender><Id>test</Id><Credential>12345</Credential></Sender>" +
                "<Recipient><Id/></Recipient>" +
                "<TransactInfo transactType=\"data\">" +
                "<TransactId>.{36}</TransactId>" +
                "<TimeStamp>.{23}</TimeStamp>" +
                "<Status/>" +
                "</TransactInfo>" +
                "<Packet>" +
                "<PacketInfo packetType=\"data\">" +
                "<PacketId>1</PacketId>" +
                "<Action>SET</Action>" +
                "<Manifest>TEST_MANIFEST</Manifest>" +
                "<Status/>" +
                "</PacketInfo>" +
                "<Payload><!\\[CDATA\\[" +
                "<Form formTypeId=\"12345\" action=\"update\" language=\"\" resumeKey=\"\" autoreq=\"\" " +
                "FirstName=\"\" LastName=\"\" email=\"\" homePhone=\"\" formId=\"\" brUID=\"\" OrderID=\"\">" +
                "<FormInput name=\"1\" title=\"FirstName\">Doh</FormInput>" +
                "<FormInput name=\"2\" title=\"LastName\">Joe</FormInput>" +
                "</Form>]]>" +
                "</Payload>" +
                "</Packet>" +
                "</Envelope>"
        );

        assertEquals(ApplicationUpdateExecutor.MEDIA_TYPE.toString(), contentType);
        assertTrue(requestString.matches(requestPattern.pattern()));
    }

    @Test
    void testFailure() throws Exception {
        server.enqueue(FAILURE_RESPONSE);

        IntegrationException exception = Assertions.assertThrows(IntegrationException.class, this::invokeUpdate);

        server.takeRequest(10, TimeUnit.SECONDS);

        assertEquals(ApplicationUpdateExecutor.CUSTOM_ERROR_CODE, exception.getCode());
        assertEquals(ApplicationUpdateExecutor.CUSTOM_ERROR_MESSAGE, exception.getMessage());

        List<String> payload = exception.getPayload();
        assertNotNull(payload);
        assertEquals(3, payload.size());
        assertEquals("405", payload.get(0));
        assertEquals("Background Request Submission Not Successful", payload.get(1));
        assertEquals("Missing Required: Applicant Region", payload.get(2));
    }

    private void invokeUpdate() throws IntegrationException {
        electricaEmulator.runIntegration(InvocationContext.builder(BrassRingApplicationAction.UPDATE)
                .authorization(AUTHORIZATION)
                .connectionProperties(MapObjectProperties.builder()
                        .addString(ApplicationUpdateExecutor.MANIFEST_PROPERTY, MANIFEST)
                        .build()
                )
                .payload(new BrassRingApplicationPayload()
                        .action(BrassRingApplicationPayload.ActionEnum.UPDATE)
                        .formTypeId(12345)
                        .addFieldsItem(new FormInput().id(1).name("FirstName").value("Doh"))
                        .addFieldsItem(new FormInput().id(2).name("LastName").value("Joe"))
                )
                .build()
        );
    }
}
