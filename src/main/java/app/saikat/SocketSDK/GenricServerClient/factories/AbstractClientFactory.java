package app.saikat.SocketSDK.GenricServerClient.factories;

import com.google.gson.Gson;

import app.saikat.SocketSDK.GenricServerClient.Client;
import app.saikat.SocketSDK.IO.MessageQueue;

public abstract class AbstractClientFactory<T extends Client> extends AbstractBaseFactory {

    public AbstractClientFactory(MessageQueue messageQueue, Gson gson) {
        super(messageQueue, gson);
    }

    public abstract T getClient(String name, String serverUrl, int serverPort);

}