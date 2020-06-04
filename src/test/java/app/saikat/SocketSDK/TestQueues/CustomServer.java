package app.saikat.SocketSDK.TestQueues;

import java.net.ServerSocket;

import app.saikat.SocketSDK.GenricServerClient.Server;
import app.saikat.SocketSDK.IO.MessageQueue;

public class CustomServer extends Server {

	public CustomServer() {
		super("", 0, null, null); // Only for testing queues
	}

	public void setMessageQueue(MessageQueue queue) {
		this.inputQueue = queue;
	}

	public MessageQueue getMessageQueue() {
		return this.inputQueue;
	}

	@Override
	public ServerSocket createServer(int port) {
		return null;
	}

}
