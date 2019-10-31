package app.saikat.SocketSDK.Instances.InsecureClient;

import java.net.Socket;

import javax.net.SocketFactory;

import com.google.gson.Gson;

import app.saikat.SocketSDK.GenricServerClient.Client;
import app.saikat.SocketSDK.IO.MessageQueue;

public class InsecureClient extends Client {

	protected InsecureClient(String name, String serverUrl, int serverPort, MessageQueue inputQueue, Gson gson) {
		super(name, serverUrl, serverPort, inputQueue, gson);
	}

	@Override
	public Socket connectToSocket(String serverUrl, int serverPort) {
		SocketFactory socketFactory = SocketFactory.getDefault();

		try {
			return socketFactory.createSocket(serverUrl, serverPort);
		} catch (Exception e) {
			logger.error("Error:", e);
			return null;
		}
	}

}