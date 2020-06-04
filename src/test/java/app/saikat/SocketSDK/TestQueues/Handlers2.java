package app.saikat.SocketSDK.TestQueues;

import javax.inject.Singleton;

import app.saikat.Annotations.SocketSDK.DefaultQueue;
import app.saikat.Annotations.SocketSDK.MessageHandler;
import app.saikat.SocketSDK.GenricServerClient.interfaces.Sender;

@Singleton
public class Handlers2 {

	private static int h1count = 0, h2count = 0, h3count = 0;
	
	@MessageHandler(queues = DefaultQueue.class)
	public void h1(Message1 m1) {
		h1count += 1;
	}

	@MessageHandler(queues = {Q1.class, Q2.class})
	public void h2(Message2 m2) {
		h2count += 1;
	}

	@MessageHandler(queues = Q2.class)
	protected void h3(Message3 m3, Sender s) {
		h3count += 1;
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
}
