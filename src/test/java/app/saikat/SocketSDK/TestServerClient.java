package app.saikat.SocketSDK;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.gson.Gson;

import org.junit.Test;

import app.saikat.CommonLogic.Threads.ThreadPoolManager;
import app.saikat.ConfigurationManagement.interfaces.ConfigurationManager;
import app.saikat.DIManagement.DIManager;
import app.saikat.DIManagement.Configurations.MethodAnnotationConfig;
import app.saikat.DIManagement.Configurations.ScanConfig;
import app.saikat.DIManagement.Exceptions.ClassNotUnderDIException;
import app.saikat.LogManagement.Logger;
import app.saikat.LogManagement.LoggerFactory;
import app.saikat.SocketSDK.Exceptions.WrongHandlerMethodException;
import app.saikat.SocketSDK.IO.MessageHandlers;
import app.saikat.SocketSDK.IO.MessageQueue;
import app.saikat.SocketSDK.Instances.InsecureClient;
import app.saikat.SocketSDK.Instances.InsecureServer;
import app.saikat.SocketSDK.TestMessageHandlers.MessageHandlerAnnot;
import app.saikat.SocketSDK.TestMessageHandlers.TestHandler;
import app.saikat.SocketSDK.TestMessageHandlers.TestMessage1;
import app.saikat.SocketSDK.TestMessageHandlers.TestMessage2;
import app.saikat.SocketSDK.TestMessageHandlers.TestMessage3;

public class TestServerClient {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testSdk() throws ClassNotUnderDIException, InterruptedException, IOException {
        ScanConfig config = ScanConfig.newBuilder()
                .addAnnotationConfig(MethodAnnotationConfig.getBuilder()
                        .forAnnotation(MessageHandlerAnnot.class)
                        .autoBuild(true)
                        .autoInvoke(false)
                        .checkDependency(false)
                        .build())
                .addPackagesToScan("app.saikat.ConfigurationManagement", "app.saikat.PojoCollections",
                        "app.saikat.GsonManagement", "app.saikat.CommonLogic", "app.saikat.DIManagement",
                        "app.saikat.LogManagement", "app.saikat.SocketSDK.TestMessageHandlers")
                .build();
        DIManager.initialize(config);

        MessageHandlers handlers = new MessageHandlers();
        List<Method> handlerMethods = DIManager.getAnnotatedMethods(MessageHandlerAnnot.class);

        handlerMethods.forEach(m -> {
            Class<?> parentCls = m.getDeclaringClass();
            Class<? extends Annotation> parentClsAnnotation = DIManager.getQualifierAnnotation(parentCls);

            try {
                Object obj = DIManager.get(parentCls, parentClsAnnotation);
                assertTrue("Got valid object", obj != null);

                handlers.addHandler(m, obj);
            } catch (ClassNotUnderDIException | WrongHandlerMethodException e) {
                logger.error(e);
                assertTrue("Error", false);
            }
        });

        ThreadPoolManager threadPoolManager = DIManager.get(ThreadPoolManager.class);
        MessageQueue messageQueue = new MessageQueue(handlers, threadPoolManager);
        Gson gson = DIManager.get(Gson.class);

        ConfigurationManager configurationManager = DIManager.get(ConfigurationManager.class);
        configurationManager.syncConfigurations();

        InsecureServer server = new InsecureServer("TestServer", 5000, gson, messageQueue);
        InsecureClient client = new InsecureClient("TestClient", null, 5000, messageQueue, gson);

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
        fileContents = fileContents.stream().filter(line -> !line.startsWith("\"timestamp\":")).collect(Collectors.toList());

        File ref = new File("referenceFile.txt");
        List<String> refContents = Lists.newArrayList(new String(Files.readAllBytes(ref.toPath()), "utf-8").split("\n"));
        refContents = fileContents.stream().filter(line -> !line.startsWith("\"timestamp\":")).collect(Collectors.toList());

        assertArrayEquals("Comparing messages received", refContents.toArray(), fileContents.toArray());
    }
}