package io.electrica.connector.brassring.application.v1.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Packet {

    @JacksonXmlProperty(localName = "PacketInfo")
    private PacketInfo packetInfo;

    @JacksonXmlCData
    @JacksonXmlProperty(localName = "Payload")
    private String payload;

    public static Packet of(String manifest, String payload) {
        Packet r = new Packet();
        r.setPacketInfo(PacketInfo.of(manifest));
        r.setPayload(payload);
        return r;
    }

    @Getter
    @Setter
    private static class PacketInfo {

        @JacksonXmlProperty(isAttribute = true, localName = "packetType")
        private String packetType = "data";

        @JacksonXmlProperty(localName = "PacketId")
        private String packetId = "1";

        @JacksonXmlProperty(localName = "Action")
        private String action = "SET";

        @JacksonXmlProperty(localName = "Manifest")
        private String manifest;

        @JacksonXmlProperty(localName = "Status")
        private Status status;

        private static PacketInfo of(String manifest) {
            PacketInfo r = new PacketInfo();
            r.setManifest(manifest);
            return r;
        }
    }
}
