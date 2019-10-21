package app.saikat.SocketSDK.GenricServerClient.Factories;

import com.google.gson.Gson;

import app.saikat.SocketSDK.IO.MessageQueue;

public abstract class AbstractBaseFactory {

    protected final MessageQueue messageQueue;
    protected final Gson gson;

    public AbstractBaseFactory(MessageQueue messageQueue, Gson gson) {
        this.messageQueue = messageQueue;
        this.gson = gson;
    }
}