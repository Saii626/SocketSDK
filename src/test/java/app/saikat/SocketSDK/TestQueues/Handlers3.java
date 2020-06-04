package app.saikat.SocketSDK.TestQueues;

import java.util.UUID;

import javax.inject.Singleton;

import app.saikat.Annotations.DIManagement.NoQualifier;
import app.saikat.Annotations.SocketSDK.MessageHandler;

@Singleton
public class Handlers3 {
	
	private static int h1count = 0, h2count = 0;

	@MessageHandler(queues = {})
	private void h1(Message1 m1, UUID session) {
		h1count += 1;
	}

	@MessageHandler(queues = NoQualifier.class)
	private void h2(Message2 m2) {
		h2count += 1;
	}

	public static int getH1count() {
		return h1count;
	}

	public static int getH2count() {
		return h2count;
	}
}
