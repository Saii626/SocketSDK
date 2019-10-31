package app.saikat.SocketSDK.CommonFiles;

import java.util.UUID;

public final class SessionData implements AutoCloseable {

	private static ThreadLocal<UUID> sessionInfo = new ThreadLocal<>();

	// Required for autoclosable feature try with resources
	private static SessionData sessionData = new SessionData();

	private SessionData() { throw new RuntimeException(); }

	public static UUID getCurrentSession() {
		return sessionInfo.get();
	}

	public static void setCurentSession(UUID uuid) {
		sessionInfo.set(uuid);
	}

	public static void removeSession() {
		sessionInfo.remove();
	}

	// Required for autoclosable feature
	public SessionData getInstance() {
		return sessionData;
	}

	@Override
	public void close() throws Exception {
		SessionData.removeSession();
	}
}