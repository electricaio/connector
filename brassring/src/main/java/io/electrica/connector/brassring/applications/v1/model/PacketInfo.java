package io.electrica.connector.brassring.applications.v1.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PacketInfo {

    private String packetType;
    private String packetId;
    private String action;
    private String manifest;
    private Status status;
}
