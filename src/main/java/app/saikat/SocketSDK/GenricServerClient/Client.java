package app.saikat.SocketSDK.GenricServerClient;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.gson.Gson;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import app.saikat.Annotations.ThreadManagement.Stats;
import app.saikat.PojoCollections.SocketMessages.Context;
import app.saikat.PojoCollections.SocketMessages.InfoMessage;
import app.saikat.SocketSDK.CommonFiles.Message;
import app.saikat.SocketSDK.CommonFiles.Statistics;
import app.saikat.SocketSDK.IO.MessageQueue;

public abstract class Client extends SocketTransceiver {

	protected Thread readerThread;
	protected MessageQueue messageQueue;

	protected Gson gson;
	protected Socket socket;

	protected String serverUrl;
	protected int serverPort;
	private String serverName;
	protected AtomicBoolean running;

	protected Logger logger = LogManager.getLogger(this.getClass());
	protected Statistics clientStatistics;

	public Client(String name, String serverUrl, int serverPort, MessageQueue inputQueue, Gson gson) {
		this.serverName = name;
		this.serverUrl = serverUrl;
		this.serverPort = serverPort;

		this.messageQueue = inputQueue;
		this.gson = gson;
		this.running = new AtomicBoolean();

		this.readerThread = new Thread(this::startClient);
		this.readerThread.setName(name + "_reader");
		this.clientStatistics = new Statistics();
	}

	@Stats
	private void printStats(Logger logger) {
		logger.printf(Level.INFO, "Client{name=%s, startT=%d, in=%d, out=%d}", serverName,
				clientStatistics.getStartTime(), clientStatistics.getTotalMessagesReceived(),
				clientStatistics.getTotalMessagesSent());
	}

	public abstract Socket connectToSocket(String serverUrl, int serverPort);

	@Override
	protected Socket getSocket() {
		return this.socket;
	}

	@Override
	public String getName() {
		return this.serverName;
	}

	@Override
	public void messageReceived(Message msg) {
		clientStatistics.msgReceived();
		messageQueue.addObjectToInputQueue(msg, this);
	}

	@Override
	protected Gson getGson() {
		return this.gson;
	}

	/**
	 * Initiates connection to server
	 */
	public void beginConnection() {
		this.readerThread.start();
	}

	/**
	 * Stops the client
	 * @throws IOException if unable to shutdown
	 * @throws InterruptedException if the thread was interrupted while waiting
	 */
	@Override
	public void stop() throws IOException, InterruptedException {
		send(new InfoMessage(Context.STATUS, "Lifecycle message", "Client shutting down"));

		// Begin client shutdown
		super.stop();
		running.set(false);
		clientStatistics.threadStopping();
	}

	@Override
	public <T> void send(T data, UUID id) throws IOException {
		super.send(data, id);
		clientStatistics.msgSent();
	}

	// /**
	//  * Override this for custom handling of message received. Calling
	//  * super.messageReceived(msg) will add the message to inputQueue
	//  * @param msg message that the client sent
	//  */
	// public void messageReceived(Message msg) {
	//	 messageQueue.addObjectToInputQueue(msg, this);
	// }

	// /**
	//  * Stops the client
	//  * @throws IOException if unable to shutdown
	//  * @throws InterruptedException if the thread was interrupted while waiting
	//  */
	// public void stopClient() throws IOException, InterruptedException {
	//	 send(new InfoMessage(Context.STATUS, "Lifecycle message", "Client shutting down"));

	//	 // Begin server shutdown
	//	 running.set(false);
	//	 senderReceiver.stop();

	//	 while (readerThread.isAlive()) {
	//		 readerThread.join(5000);
	//		 logger.warn("{} still alive", readerThread.getName());
	//	 }
	//	 clientStatistics.threadStopping();
	// }

	private void startClient() {
		clientStatistics.threadStarted();
		running.set(true);

		while (running.get()) {
			try {
				Thread.sleep(2000);

				this.socket = connectToSocket(serverUrl, serverPort);
				if (this.socket == null)
					return;

				this.startReading(running);
			} catch (Exception e) {
				logger.error(e);
			}

		}
	}

}