package app.saikat.SocketSDK.Instances.SSLClient;

import javax.inject.Inject;

import com.google.gson.Gson;

import app.saikat.SocketSDK.GenricServerClient.Factories.AbstractClientFactory;
import app.saikat.SocketSDK.IO.MessageQueue;

public class SSLClientFactory extends AbstractClientFactory<SSLClient> {

    @Inject
    public SSLClientFactory(MessageQueue messageQueue, Gson gson) {
        super(messageQueue, gson);
    }

    @Override
    public SSLClient getClient(String name, String serverUrl, int serverPort) {
        return new SSLClient(name, serverUrl, serverPort, messageQueue, gson);
    }

}