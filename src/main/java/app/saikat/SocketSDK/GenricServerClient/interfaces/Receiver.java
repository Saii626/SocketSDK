package app.saikat.SocketSDK.GenricServerClient.interfaces;

import java.util.concurrent.atomic.AtomicBoolean;

import app.saikat.SocketSDK.IO.Message;

public interface Receiver extends Name {

    /**
     * Override this for custom handling of message received. Calling
     * super.messageReceived(msg) will add the message to inputQueue
     * @param msg message that the client sent
     */
    void messageReceived(Message msg);

    /**
     * Reads a message from inputstream
     * @return
     */
    Message read();

    /**
     * Starts reading from input stream in loop
     * @param running an atomic boolean value, which if true continues reading and
     *                stops when false
     */
    default void startReading(AtomicBoolean running) {

        while (running.get()) {
            Message object = this.read();
            if (object != null) {
                messageReceived(object);
            }
        }
    }

}