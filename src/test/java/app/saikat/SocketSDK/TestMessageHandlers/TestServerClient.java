package app.saikat.SocketSDK.TestMessageHandlers;

import javax.inject.Inject;

import app.saikat.Annotations.DIManagement.Generator;
import app.saikat.Annotations.SocketSDK.DefaultQueue;
import app.saikat.SocketSDK.IO.MessageQueue;
import app.saikat.SocketSDK.Instances.InsecureClient;
import app.saikat.SocketSDK.Instances.InsecureServer;

public class TestServerClient {

	private Generator<InsecureServer> serverGenerator;
	private Generator<InsecureClient> clientGenerator;
	private MessageQueue queue;

	@Inject
	public TestServerClient(Generator<InsecureServer> serverGenerator, Generator<InsecureClient> clientGenerator,
			@DefaultQueue MessageQueue queue) {
		this.serverGenerator = serverGenerator;
		this.clientGenerator = clientGenerator;
		this.queue = queue;
	}

	public InsecureServer createServer(String name, int port) {
		return serverGenerator.generate(name, port, queue);
	}

	public InsecureClient createClient(String name, String url, int port) {
		return clientGenerator.generate(name, url, port, queue);
	}
}