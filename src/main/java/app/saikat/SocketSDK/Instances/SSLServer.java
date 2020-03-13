package app.saikat.SocketSDK.Instances;

import java.io.IOException;
import java.net.ServerSocket;

import javax.net.ssl.SSLServerSocketFactory;

import com.google.gson.Gson;

import app.saikat.Annotations.DIManagement.GenParam;
import app.saikat.Annotations.DIManagement.Generate;
import app.saikat.SocketSDK.GenricServerClient.Server;
import app.saikat.SocketSDK.IO.MessageQueue;

public class SSLServer extends Server {

	@Generate
	public SSLServer(@GenParam String name, @GenParam int port, Gson gson, MessageQueue inputQueue) {
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
