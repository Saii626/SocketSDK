package app.saikat.SocketSDK.GenricServerClient.Factories;

import com.google.gson.Gson;

import app.saikat.SocketSDK.GenricServerClient.Server;
import app.saikat.SocketSDK.IO.MessageHandlers;
import app.saikat.SocketSDK.IO.MessageQueue;

public abstract class AbstractServerFactory<T extends Server> extends AbstractBaseFactory {

    public AbstractServerFactory(MessageQueue messageQueue, Gson gson) {
        super(messageQueue, gson);
    }

    public abstract T getServer(String name, int port) ;

}