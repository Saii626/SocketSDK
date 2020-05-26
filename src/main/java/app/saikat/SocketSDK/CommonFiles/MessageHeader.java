package app.saikat.SocketSDK.CommonFiles;

import java.util.UUID;

public class MessageHeader {

	private long timestamp;
	private UUID session;
	private String token;

	public MessageHeader(long timestamp, UUID session, String token) {
		this.timestamp = timestamp;
		this.session = session;
		this.token = token;
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

	public String getToken() {
		return token;
	}

	public void setToken(String from) {
		this.token = from;
	}
}