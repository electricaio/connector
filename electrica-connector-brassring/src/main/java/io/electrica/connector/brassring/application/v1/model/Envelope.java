package io.electrica.connector.brassring.application.v1.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.electrica.connector.spi.context.IbmAuthorization;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@JacksonXmlRootElement(localName = "Envelope")
public class Envelope {

    @JacksonXmlProperty(isAttribute = true, localName = "version")
    private String version = "01.00";

    @JacksonXmlProperty(localName = "Sender")
    private Sender sender;

    @JacksonXmlProperty(localName = "Recipient")
    private Recipient recipient;

    @JacksonXmlProperty(localName = "TransactInfo")
    private TransactInfo transactInfo;

    @JacksonXmlProperty(localName = "Packet")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Packet> packets;

    public static Envelope of(String manifest, IbmAuthorization authorization, String formPayload) {
        Envelope r = new Envelope();
        r.setSender(new Sender(authorization.getIntegrationId(), authorization.getClientId()));
        r.setRecipient(new Recipient());
        r.setTransactInfo(new TransactInfo());
        r.setPackets(Collections.singletonList(Packet.of(manifest, formPayload)));
        return r;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sender {

        @JacksonXmlProperty(localName = "Id")
        private String id;

        @JacksonXmlProperty(localName = "Credential")
        private String credential;
    }

    @Getter
    @Setter
    public static class Recipient {

        @JacksonXmlProperty(localName = "Id")
        private String id;
    }

    @Getter
    @Setter
    public static class TransactInfo {

        @JacksonXmlProperty(isAttribute = true, localName = "transactType")
        private String transactType = "data";

        @JacksonXmlProperty(localName = "TransactId")
        private String transactId = UUID.randomUUID().toString();

        @JacksonXmlProperty(localName = "TimeStamp")
        private String timeStamp = LocalDateTime.now().toString();

        @JacksonXmlProperty(localName = "Status")
        private Status status;
    }
}
