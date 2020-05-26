package app.saikat.SocketSDK.GenricServerClient.interfaces;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import app.saikat.SocketSDK.CommonFiles.Message;


public interface Receiver extends Named {

	/**
	 * Override this for custom handling of message received. Calling
	 * super.messageReceived(msg) will add the message to inputQueue
	 * @param msg message that the client sent
	 */
	void messageReceived(Message msg);

	/**
	 * Reads a message from inputstream
	 * @return the message that was currently read
	 * @throws IOException if unable to read from buffer
	 */
	Message read() throws IOException;

	/**
	 * Starts reading from input stream in loop
	 * @param running an atomic boolean value, which if true continues reading and
	 *				stops when false
	 * @throws IOException if unable to read from buffer
	 */
	default void startReading(AtomicBoolean running) throws IOException {

		while (running.get()) {
			Message object = this.read();
			if (object != null) {
				messageReceived(object);
			}
		}
	}

}