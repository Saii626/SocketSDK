package app.saikat.SocketSDK.Instances;

import java.io.IOException;
import java.net.ServerSocket;

import com.google.gson.Gson;

import app.saikat.Annotations.DIManagement.GenParam;
import app.saikat.Annotations.DIManagement.Generate;
import app.saikat.SocketSDK.GenricServerClient.Server;
import app.saikat.SocketSDK.IO.MessageQueue;

public class InsecureServer extends Server {

	@Generate
	public InsecureServer(@GenParam String name, @GenParam int port, Gson gson, @GenParam MessageQueue inputQueue) {
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
