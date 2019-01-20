package io.electrica.connector.brassring.v1.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Packet {

    private PacketInfo packetInfo;

    @JacksonXmlCData
    private String payload;
}
