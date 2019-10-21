package app.saikat.SocketSDK.IO.Providers;

import app.saikat.CommonLogic.Threads.ThreadPoolManager;
import app.saikat.DIManagement.Provides;
import app.saikat.SocketSDK.IO.MessageHandlers;
import app.saikat.SocketSDK.IO.MessageQueue;

public class MessageQueueProvider {

    @Provides
    public MessageQueue getMessageQueue(MessageHandlers handlers, ThreadPoolManager manager) {
        return new MessageQueue(handlers, manager);
    }
}