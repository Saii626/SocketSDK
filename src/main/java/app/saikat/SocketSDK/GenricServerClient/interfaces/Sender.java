package app.saikat.SocketSDK.GenricServerClient.interfaces;

import java.io.IOException;
import java.util.UUID;

import app.saikat.SocketSDK.CommonFiles.SessionData;

public interface Sender extends Name {

	/**
	 * Sends an object to underlying socket. If this thread has a session, that session 
	 * is used. Else new session is created
	 * @param <T> type of object to send
	 * @param data object to send to client
	 * @throws IOException if unable to send o server
	 */
	default <T> void send(T data) throws IOException {
		UUID session = SessionData.getCurrentSession();
		if (session == null) {
			send(data, UUID.randomUUID());
		} else {
			send(data, session);
		}
	}

	/**
	 * Sends an object to underlying socket using an old session
	 * @param <T> type of object to send
	 * @param data object to send to client
	 * @param session the session of this message
	 * @throws IOException if unable to send o server
	 */
	<T> void send(T data, UUID session) throws IOException;

	
}