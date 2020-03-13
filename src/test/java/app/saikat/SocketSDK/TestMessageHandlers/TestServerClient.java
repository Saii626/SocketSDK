package app.saikat.SocketSDK.TestMessageHandlers;

import javax.inject.Inject;

import app.saikat.Annotations.DIManagement.Generator;
import app.saikat.SocketSDK.Instances.InsecureClient;
import app.saikat.SocketSDK.Instances.InsecureServer;

public class TestServerClient {

	private Generator<InsecureServer> serverGenerator;
	private Generator<InsecureClient> clientGenerator;

	@Inject
	public TestServerClient(Generator<InsecureServer> serverGenerator, Generator<InsecureClient> clientGenerator) {
		this.serverGenerator = serverGenerator;
		this.clientGenerator = clientGenerator;
	}

	public InsecureServer createServer(String name, int port) {
		return serverGenerator.generate(name, port);
	}

	public InsecureClient createClient(String name, String url, int port) {
		return clientGenerator.generate(name, url, port);
	}
}