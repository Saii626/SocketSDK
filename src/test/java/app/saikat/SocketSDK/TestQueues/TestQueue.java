package app.saikat.SocketSDK.TestQueues;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import app.saikat.Annotations.SocketSDK.DefaultQueue;
import app.saikat.DIManagement.Exceptions.BeanNotFoundException;
import app.saikat.DIManagement.Interfaces.DIManager;
import app.saikat.GsonManagement.JsonObject;
import app.saikat.SocketSDK.CommonFiles.Message;
import app.saikat.SocketSDK.CommonFiles.MessageHeader;
import app.saikat.SocketSDK.IO.MessageQueue;

public class TestQueue {

	@Test
	public void test() throws BeanNotFoundException {
		DIManager manager = DIManager.newInstance();

		manager.scan("app.saikat.DIManagement", "app.saikat.Annotations", "app.saikat.ConfigurationManagement",
				"app.saikat.PojoCollections", "app.saikat.GsonManagement", "app.saikat.ThreadManagement",
				"app.saikat.SocketSDK.GenericServerClient", "app.saikat.SocketSDK.Instances", "app.saikat.SocketSDK.IO",
				"app.saikat.SocketSDK.TestQueues");

		
		MessageQueue defaultQueue = manager.getBeanOfType(MessageQueue.class, DefaultQueue.class).getProvider().get();
		MessageQueue q1Queue = manager.getBeanOfType(MessageQueue.class, Q1.class).getProvider().get();
		MessageQueue q2Queue = manager.getBeanOfType(MessageQueue.class, Q2.class).getProvider().get();

		Message m1 = Message.containing(new MessageHeader(0, null, null), new JsonObject(new Message1()));
		Message m2 = Message.containing(new MessageHeader(0, null, null), new JsonObject(new Message2()));
		Message m3 = Message.containing(new MessageHeader(0, null, null), new JsonObject(new Message3()));


		CustomServer customServer = new CustomServer();

		//Default queue assertions
		customServer.setMessageQueue(defaultQueue);
		customServer.messageReceived(m1);
		customServer.messageReceived(m2);
		customServer.messageReceived(m3);

		assertEquals("Handler1 h1 val", Handlers1.getH1count(), 1);
		assertEquals("Handler1 h2 val", Handlers1.getH2count(), 1);
		assertEquals("Handler1 h3 val", Handlers1.getH3count(), 0);
		assertEquals("Handler1 h4 val", Handlers1.getH4count(), 1);

		assertEquals("Handler2 h1 val", Handlers2.getH1count(), 1);
		assertEquals("Handler2 h2 val", Handlers2.getH2count(), 0);
		assertEquals("Handler2 h3 val", Handlers2.getH3count(), 0);

		assertEquals("Handler3 h1 val", Handlers3.getH1count(), 0);
		assertEquals("Handler3 h2 val", Handlers3.getH2count(), 0);

		// Q1 queue
		customServer.setMessageQueue(q1Queue);
		customServer.messageReceived(m1);
		customServer.messageReceived(m2);
		customServer.messageReceived(m3);

		assertEquals("Handler1 h1 val", Handlers1.getH1count(), 1);
		assertEquals("Handler1 h2 val", Handlers1.getH2count(), 1);
		assertEquals("Handler1 h3 val", Handlers1.getH3count(), 1);
		assertEquals("Handler1 h4 val", Handlers1.getH4count(), 1);

		assertEquals("Handler2 h1 val", Handlers2.getH1count(), 1);
		assertEquals("Handler2 h2 val", Handlers2.getH2count(), 1);
		assertEquals("Handler2 h3 val", Handlers2.getH3count(), 0);

		assertEquals("Handler3 h1 val", Handlers3.getH1count(), 0);
		assertEquals("Handler3 h2 val", Handlers3.getH2count(), 0);

		// Q2 queue
		customServer.setMessageQueue(q2Queue);
		customServer.messageReceived(m1);
		customServer.messageReceived(m2);
		customServer.messageReceived(m3);

		assertEquals("Handler1 h1 val", Handlers1.getH1count(), 1);
		assertEquals("Handler1 h2 val", Handlers1.getH2count(), 1);
		assertEquals("Handler1 h3 val", Handlers1.getH3count(), 1);
		assertEquals("Handler1 h4 val", Handlers1.getH4count(), 2);

		assertEquals("Handler2 h1 val", Handlers2.getH1count(), 1);
		assertEquals("Handler2 h2 val", Handlers2.getH2count(), 2);
		assertEquals("Handler2 h3 val", Handlers2.getH3count(), 1);

		assertEquals("Handler3 h1 val", Handlers3.getH1count(), 0);
		assertEquals("Handler3 h2 val", Handlers3.getH2count(), 0);
	}

}
