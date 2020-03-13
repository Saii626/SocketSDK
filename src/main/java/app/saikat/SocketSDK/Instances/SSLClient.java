package app.saikat.SocketSDK.Instances;

import java.net.Socket;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import com.google.gson.Gson;

import app.saikat.Annotations.DIManagement.GenParam;
import app.saikat.Annotations.DIManagement.Generate;
import app.saikat.SocketSDK.GenricServerClient.Client;
import app.saikat.SocketSDK.IO.MessageQueue;

public class SSLClient extends Client {

	@Generate
	public SSLClient(@GenParam String name, @GenParam String serverUrl, @GenParam int serverPort,
			MessageQueue inputQueue, Gson gson) {
		super(name, serverUrl, serverPort, inputQueue, gson);
	}

	@Override
	public Socket connectToSocket(String serverUrl, int serverPort) {
		SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

		try {
			SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket(serverUrl, serverPort);
			socket.startHandshake();

			return socket;
		} catch (Exception e) {
			logger.error("Error:", e);
			return null;
		}
	}

}