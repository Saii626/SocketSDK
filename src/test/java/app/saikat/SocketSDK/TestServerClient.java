package app.saikat.SocketSDK;

import static org.junit.Assert.assertArrayEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.gson.Gson;

import org.junit.Test;

import app.saikat.DIManagement.DIManager;
import app.saikat.DIManagement.Configurations.MethodAnnotationConfig;
import app.saikat.DIManagement.Configurations.ScanConfig;
import app.saikat.DIManagement.Exceptions.ClassNotUnderDIException;
import app.saikat.SocketSDK.Instances.InsecureClient.InsecureClient;
import app.saikat.SocketSDK.Instances.InsecureClient.InsecureClientFactory;
import app.saikat.SocketSDK.Instances.InsecureServer.InsecureServer;
import app.saikat.SocketSDK.Instances.InsecureServer.InsecureServerFactory;
import app.saikat.SocketSDK.TestMessageHandlers.MessageHandlerAnnot;
import app.saikat.SocketSDK.TestMessageHandlers.TestHandler;
import app.saikat.SocketSDK.TestMessageHandlers.TestMessage1;
import app.saikat.SocketSDK.TestMessageHandlers.TestMessage2;
import app.saikat.SocketSDK.TestMessageHandlers.TestMessage3;

public class TestServerClient {

    @Test
    public void testSdk() throws ClassNotUnderDIException, InterruptedException, IOException {
        ScanConfig config = ScanConfig.newBuilder()
                .addAnnotationConfig(MethodAnnotationConfig.getBuilder()
                        .forAnnotation(MessageHandlerAnnot.class)
                        .autoBuild(true)
                        .autoInvoke(false)
                        .checkDependency(false)
                        .build())
                .addPackagesToScan("app.saikat")
                .build();
        DIManager.initialize(config);

        InsecureServerFactory serverFactory = DIManager.get(InsecureServerFactory.class);
        InsecureClientFactory clientFactory = DIManager.get(InsecureClientFactory.class);
        InsecureServer server = serverFactory.getServer("TestServer", 5000);
        InsecureClient client = clientFactory.getClient("TestClient", null, 5000);

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
        TestHandler handler = DIManager.get(TestHandler.class);
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