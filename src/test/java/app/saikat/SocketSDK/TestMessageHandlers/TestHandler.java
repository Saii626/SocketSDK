package app.saikat.SocketSDK.TestMessageHandlers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gson.Gson;

import app.saikat.Annotations.SocketSDK.MessageHandler;
import app.saikat.SocketSDK.CommonFiles.InfoMessage;
import app.saikat.SocketSDK.CommonFiles.MessageHeader;
import app.saikat.SocketSDK.GenricServerClient.interfaces.Sender;

@Singleton
public class TestHandler {

	private Gson gson;
	private File file;
	private BufferedOutputStream out;
	private byte[] msgTerminator = "\n-------------------------------------------------------------------------------------------------\n"
			.getBytes();

	@Inject
	public TestHandler(Gson gson) throws FileNotFoundException {
		this.gson = gson;

		this.file = new File("testFile.txt");
		FileOutputStream fout = new FileOutputStream(this.file);
		out = new BufferedOutputStream(fout);
	}

	@MessageHandler
	public void handleMessage1(TestMessage1 testMessage1, Sender sendable, MessageHeader header) throws IOException {
		String receivedMsg = this.gson.toJson(testMessage1);
		String headerInfo = this.gson.toJson(header);
		out.write(String.format("%s:\n %s\n", sendable.getName(), headerInfo).getBytes());
		out.write(String.format("%s:\n %s", sendable.getName(), receivedMsg).getBytes());
		out.write(msgTerminator);

		sendable.send(new TestMessage3(testMessage1, null, 1542));
	}

	@MessageHandler
	public void handleMessage2(Sender sendable, TestMessage2 testMessage2, MessageHeader header) throws IOException {
		String receivedMsg = this.gson.toJson(testMessage2);
		String headerInfo = this.gson.toJson(header);
		out.write(String.format("%s:\n %s\n", sendable.getName(), headerInfo).getBytes());
		out.write(String.format("%s:\n %s", sendable.getName(), receivedMsg).getBytes());
		out.write(msgTerminator);

		sendable.send(new TestMessage3(null, testMessage2, 5642));
	}

	@MessageHandler
	public void handleMessage3(Sender sendable, TestMessage3 testMessage3, UUID id, MessageHeader header) throws IOException {
		String receivedMsg = this.gson.toJson(testMessage3);
		String headerInfo = this.gson.toJson(header);
		out.write(String.format("%s:\n %s\n", sendable.getName(), headerInfo).getBytes());
		out.write(String.format("%s:\n %s", sendable.getName(), receivedMsg).getBytes());
		out.write(msgTerminator);
	}

	@MessageHandler
	public void handleInfoMessage(Sender sendable, InfoMessage infoMsg, UUID id, MessageHeader header) throws IOException {
		String receivedMsg = this.gson.toJson(infoMsg);
		String headerInfo = this.gson.toJson(header);
		out.write(String.format("%s:\n %s\n", sendable.getName(), headerInfo).getBytes());
		out.write(String.format("%s:\n %s", sendable.getName(), receivedMsg).getBytes());
		out.write(msgTerminator);
	}


	public void endTest() throws IOException {
		out.flush();
		out.close();
	}
}