package app.saikat.SocketSDK.GenricServerClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.gson.Gson;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import app.saikat.Annotations.ThreadManagement.Stats;
import app.saikat.SocketSDK.CommonFiles.InfoMessage;
import app.saikat.SocketSDK.CommonFiles.Message;
import app.saikat.SocketSDK.CommonFiles.Statistics;
import app.saikat.SocketSDK.IO.MessageQueue;

public abstract class Server extends SocketTransceiver {

	protected Thread readerThread;
	protected MessageQueue inputQueue;

	protected Gson gson;
	protected Socket socket;
	protected SocketTransceiver senderReceiver;

	protected String serverName;
	protected int PORT;
	protected AtomicBoolean running;

	protected Logger logger = LogManager.getLogger(this.getClass());
	protected Statistics serverStatistics;

	public Server(String name, int port, Gson gson, MessageQueue inputQueue) {
		this.gson = gson;
		this.PORT = port;
		this.serverName = name;
		this.inputQueue = inputQueue;
		running = new AtomicBoolean();

		readerThread = new Thread(this::initServer);
		this.readerThread.setName(name+"_reader");
		readerThread.setName(name);
		serverStatistics = new Statistics();
	}

	@Stats
	private void printStats(Logger logger) {
		logger.printf(Level.INFO, "Server{name=%s, startT=%d, in=%d, out=%d}", serverName,
				serverStatistics.getStartTime(), serverStatistics.getTotalMessagesReceived(),
				serverStatistics.getTotalMessagesSent());
	}

	@Override
	public String getName() {
		return this.serverName;
	}

	@Override
	protected Socket getSocket() {
		return this.socket;
	}

	@Override
	protected Gson getGson() {
		return this.gson;
	}

	public int getPORT() {
		return PORT;
	}

	/**
	 * Create the server
	 * @param port the port on which to listen to
	 * @return ServerSocket of the created server
	 */
	public abstract ServerSocket createServer(int port);

	/**
	 * Begins the server and start listening on port
	 */
	public void startServer() {
		readerThread.start();
	}

	/**
	 * Sends an object to connected client using an old session
	 * @param <T> type of object to send
	 * @param object object to send to client
	 * @param session the session of this message
	 * @throws IOException if unable to send o server
	 */
	@Override
	public <T> void send(T object, UUID session) throws IOException {
		super.send(object, session);
		serverStatistics.msgSent();
	}

	/**
	 * Stops the server
	 * @throws IOException if unable to shutdown
	 * @throws InterruptedException if the thread was interrupted while waiting
	 */
	public void stop() throws IOException, InterruptedException {
		send(new InfoMessage("Lifecycle message", "Server shutting down"));

		// Begin server shutdown
		running.set(false);
		super.stop();
		serverStatistics.threadStopping();
	}

	/**
	 * Gets server statistics
	 * @return server statistics
	 */
	public Statistics getServerStatistics() {
		return serverStatistics;
	}

	/**
	 * Override this for custom handling of message received. Calling
	 * super.messageReceived(msg) will add the message to inputQueue
	 * @param msg message that the client sent
	 */
	public void messageReceived(Message msg) {
		inputQueue.addObjectToInputQueue(msg, this);
	}

	private void initServer() {
		serverStatistics.threadStarted();
		running.set(true);

		try {
			ServerSocket serverSocket = createServer(PORT);
			if (serverSocket == null)
				return;

			this.PORT = serverSocket.getLocalPort();
			while (running.get()) {

				this.socket = serverSocket.accept();
				this.startReading(running);
			}

		} catch (Exception e) {
			logger.error(e);
		}
	}
}
