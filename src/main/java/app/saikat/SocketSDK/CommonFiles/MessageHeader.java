package app.saikat.SocketSDK.CommonFiles;

import java.util.UUID;

public class MessageHeader {

    private long timestamp;
    private UUID session;
    private String from;

    public MessageHeader(long timestamp, UUID session, String from) {
        this.timestamp = timestamp;
        this.session = session;
        this.from = from;
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