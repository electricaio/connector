package io.electrica.connector.brassring.v1.model;

import java.util.Arrays;

public class Envelope {

    private String version;
    private Sender sender;
    private Recipient recipient;
    private TransactInfo transactInfo;
    private Packet[] packets;

    public Envelope() {
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public Recipient getRecipient() {
        return recipient;
    }

    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }

    public TransactInfo getTransactInfo() {
        return transactInfo;
    }

    public void setTransactInfo(TransactInfo transactInfo) {
        this.transactInfo = transactInfo;
    }

    public Packet[] getPackets() {
        return Arrays.stream(packets)
                .map(packet -> packet == null ? null : new Packet(packet.getPacketInfo(), packet.getPayload()))
                .toArray(Packet[]::new);
    }

    public void setPackets(Packet[] packets) {
        this.packets = Arrays.stream(packets)
                .map(packet -> packet == null ? null : new Packet(packet.getPacketInfo(), packet.getPayload()))
                .toArray(Packet[]::new);
    }
}


