package app.saikat.SocketSDK;

import static org.junit.Assert.assertArrayEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.junit.Test;

import app.saikat.Annotations.SocketSDK.MessageHandler;
import app.saikat.DIManagement.Exceptions.BeanNotFoundException;
import app.saikat.DIManagement.Interfaces.DIBean;
import app.saikat.DIManagement.Interfaces.DIManager;
import app.saikat.SocketSDK.Instances.InsecureClient;
import app.saikat.SocketSDK.Instances.InsecureServer;
import app.saikat.SocketSDK.TestMessageHandlers.TestHandler;
import app.saikat.SocketSDK.TestMessageHandlers.TestMessage1;
import app.saikat.SocketSDK.TestMessageHandlers.TestMessage2;
import app.saikat.SocketSDK.TestMessageHandlers.TestMessage3;
import app.saikat.SocketSDK.TestMessageHandlers.TestServerClient;

public class TestSDK {

	@Test
	public void testSdk() throws InterruptedException, IOException, BeanNotFoundException {
		DIManager manager = DIManager.newInstance();
		manager.scan("app.saikat");

		Set<DIBean<?>> handlerBeans = manager.getBeansWithType(MessageHandler.class);
		System.out.println("handlers.size = " + handlerBeans.size());

		TestHandler handler = manager.getBeanOfType(TypeToken.of(TestHandler.class)).getProvider().get();
		TestServerClient serverClient = manager.getBeanOfType(TypeToken.of(TestServerClient.class)).getProvider().get();

		InsecureServer server = serverClient.createServer("TestServer", 5000);
		InsecureClient client = serverClient.createClient("TestClient", null, 5000);

		TestMessage1 msg1 = new TestMessage1("hello", 6546, 125.59f, 54132.136646867, 'h');
		TestMessage2 msg2 = new TestMessage2(Gson.class, "world");
		server.startServer();
		Thread.sleep(3000);
		client.beginConnection();
		Thread.sleep(3000);

		client.send(msg1);
		Thread.sleep(1000);
		server.send(msg2);
		Thread.sleep(1000);

		UUID id = UUID.randomUUID();
		server.send(new TestMessage3(msg1, msg2, 5665), id);
		Thread.sleep(1000);

		client.stop();
		Thread.sleep(1000);
		server.stop();
		Thread.sleep(1000);
		
		handler.endTest();

		File f = new File("testFile.txt");
		List<String> fileContents = Lists.newArrayList(new String(Files.readAllBytes(f.toPath()), "utf-8").split("\n"));
		fileContents = fileContents.stream().filter(line -> !(line.contains("\"timestamp\":") || line.contains("\"session\":"))).collect(Collectors.toList());

		File ref = new File("referenceFile.txt");
		List<String> refContents = Lists.newArrayList(new String(Files.readAllBytes(ref.toPath()), "utf-8").split("\n"));
		refContents = refContents.stream().filter(line -> !(line.contains("\"timestamp\":") || line.contains("\"session\":"))).collect(Collectors.toList());

		assertArrayEquals("Comparing messages received", refContents.toArray(), fileContents.toArray());
	}
}