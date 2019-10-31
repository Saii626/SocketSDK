package app.saikat.SocketSDK.Instances.InsecureServer;

import java.io.IOException;
import java.net.ServerSocket;

import com.google.gson.Gson;

import app.saikat.SocketSDK.GenricServerClient.Server;
import app.saikat.SocketSDK.IO.MessageQueue;

public class InsecureServer extends Server {

	protected InsecureServer(String name, int port, Gson gson, MessageQueue inputQueue) {
		super(name, port, gson, inputQueue);
	}

	@Override
	public ServerSocket createServer(int port) {
		try {
			return new ServerSocket(port);
		} catch (IOException e) {
			logger.error("Error:", e);
			return null;
		}
	}

	// @Override
	// public boolean validateClient() {
	//	 return true;
	// }

}
