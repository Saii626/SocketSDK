package app.saikat.SocketSDK.Instances.InsecureClient;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gson.Gson;

import app.saikat.SocketSDK.GenricServerClient.factories.AbstractClientFactory;
import app.saikat.SocketSDK.IO.MessageQueue;

@Singleton
public class InsecureClientFactory extends AbstractClientFactory<InsecureClient> {

	@Inject
	public InsecureClientFactory(MessageQueue messageQueue, Gson gson) {
		super(messageQueue, gson);
	}

	@Override
	public InsecureClient getClient(String name, String serverUrl, int serverPort) {
		return new InsecureClient(name, serverUrl, serverPort, messageQueue, gson);
	}

}