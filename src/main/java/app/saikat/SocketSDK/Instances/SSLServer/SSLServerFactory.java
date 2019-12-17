package app.saikat.SocketSDK.Instances.SSLServer;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gson.Gson;

import app.saikat.SocketSDK.GenricServerClient.factories.AbstractServerFactory;
import app.saikat.SocketSDK.IO.MessageQueue;

@Singleton
public class SSLServerFactory extends AbstractServerFactory<SSLServer> {

	@Inject
	public SSLServerFactory(MessageQueue messageQueue, Gson gson) {
		super(messageQueue, gson);
	}

	@Override
	public SSLServer getServer(String name, int port) {
		return new SSLServer(name, port, gson, messageQueue);
	}

}