package app.saikat.SocketSDK.Instances.InsecureServer;

import javax.inject.Inject;

import com.google.gson.Gson;

import app.saikat.SocketSDK.GenricServerClient.factories.AbstractServerFactory;
import app.saikat.SocketSDK.IO.MessageQueue;

public class InsecureServerFactory extends AbstractServerFactory<InsecureServer> {

	@Inject
	public InsecureServerFactory(MessageQueue messageQueue, Gson gson) {
		super(messageQueue, gson);
	}

	@Override
	public InsecureServer getServer(String name, int port) {
		return new InsecureServer(name, port, gson, messageQueue);
	}

}