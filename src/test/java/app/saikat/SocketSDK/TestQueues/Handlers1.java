package app.saikat.SocketSDK.TestQueues;

import javax.inject.Singleton;

import app.saikat.Annotations.SocketSDK.DefaultQueue;
import app.saikat.Annotations.SocketSDK.MessageHandler;
import app.saikat.SocketSDK.CommonFiles.MessageHeader;
import app.saikat.SocketSDK.GenricServerClient.Server;
import app.saikat.SocketSDK.GenricServerClient.interfaces.Sender;

@Singleton
public class Handlers1 {

	private static int h1count = 0;
	private static int h2count = 0;
	private static int h3count = 0;
	private static int h4count = 0;

	@MessageHandler
	private void h1(Message1 m1, Sender s) {
		h1count += 1;
	}

	@MessageHandler
	private void h2(Message1 m1) {
		h2count += 1;
	}

	@MessageHandler(queues = Q1.class)
	private void h3(Message1 m1, MessageHeader header) {
		h3count += 1;
	}

	@MessageHandler(queues = { DefaultQueue.class, Q2.class })
	private void h4(Message2 m2, Server s) {
		h4count += 1;
	}

	public static int getH1count() {
		return h1count;
	}

	public static int getH2count() {
		return h2count;
	}

	public static int getH3count() {
		return h3count;
	}

	public static int getH4count() {
		return h4count;
	}
}
