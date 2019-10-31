package app.saikat.SocketSDK.Instances.SSLServer;

import java.io.IOException;
import java.net.ServerSocket;

import javax.net.ssl.SSLServerSocketFactory;

import com.google.gson.Gson;

import app.saikat.SocketSDK.GenricServerClient.Server;
import app.saikat.SocketSDK.IO.MessageQueue;

public class SSLServer extends Server {

	protected SSLServer(String name, int port, Gson gson, MessageQueue inputQueue) {
		super(name, port, gson, inputQueue);
	}

	@Override
	public ServerSocket createServer(int port) {
		SSLServerSocketFactory sslSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		try {
			return sslSocketFactory.createServerSocket(port);
		} catch (IOException e) {
			logger.error("Error: {}", e);
			return null;
		}
	}

	// @Override
	// public boolean validateClient() {
	//	 return false;
	// }
}
