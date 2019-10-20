package app.saikat.SocketSDK.CommonFiles;

import java.util.UUID;

public class MessageHeader {

    private byte[] msgDigest;
    private long timestamp;
    private UUID session;
    private String from;

    public MessageHeader(long timestamp, UUID session, byte[] msgDigest, String from) {
        this.msgDigest = msgDigest;
        this.timestamp = timestamp;
        this.session = session;
        this.from = from;
    }

    public byte[] getMsgDigest() {
        return this.msgDigest;
    }

    public void setMsgDigest(byte[] msgDigest) {
        this.msgDigest = msgDigest;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public UUID getSession() {
        return this.session;
    }

    public void setSession(UUID session) {
        this.session = session;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}