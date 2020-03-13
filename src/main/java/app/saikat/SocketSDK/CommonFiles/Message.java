package app.saikat.SocketSDK.CommonFiles;

import app.saikat.GsonManagement.JsonObject;
import app.saikat.PojoCollections.CommonObjects.Tuple;
import app.saikat.SocketSDK.CommonFiles.MessageHeader;

public class Message extends Tuple<MessageHeader, JsonObject> {

	public Message(MessageHeader first, JsonObject second) {
		super(first, second);
	}

	public static Message containing(MessageHeader header, JsonObject object) {
		return new Message(header, object);
	}
}