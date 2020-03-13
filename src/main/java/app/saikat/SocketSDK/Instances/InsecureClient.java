package app.saikat.SocketSDK.Instances;

import java.net.Socket;

import javax.net.SocketFactory;

import com.google.gson.Gson;

import app.saikat.Annotations.DIManagement.GenParam;
import app.saikat.Annotations.DIManagement.Generate;
import app.saikat.SocketSDK.GenricServerClient.Client;
import app.saikat.SocketSDK.IO.MessageQueue;

public class InsecureClient extends Client {

	@Generate
	public InsecureClient(@GenParam String name, @GenParam String serverUrl, @GenParam int serverPort, MessageQueue inputQueue, Gson gson) {
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