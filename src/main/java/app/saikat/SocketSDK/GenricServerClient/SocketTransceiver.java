package app.saikat.SocketSDK.GenricServerClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.gson.Gson;

import app.saikat.GsonManagement.JsonObject;
import app.saikat.LogManagement.Logger;
import app.saikat.LogManagement.LoggerFactory;
import app.saikat.SocketSDK.CommonFiles.MessageHeader;
import app.saikat.SocketSDK.GenricServerClient.interfaces.Receiver;
import app.saikat.SocketSDK.GenricServerClient.interfaces.Sender;
import app.saikat.SocketSDK.IO.Message;

public abstract class SocketTransceiver implements Sender, Receiver {

    private AtomicBoolean isReading = new AtomicBoolean();
    private AtomicBoolean isWriting = new AtomicBoolean();

    private Optional<Thread> readerThread = Optional.empty();
    private Optional<Thread> writerThread = Optional.empty();

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    protected abstract Socket getSocket();

    protected abstract Gson getGson();

    public void stop() throws IOException, InterruptedException {
        this.getSocket()
                .shutdownInput();
        this.getSocket()
                .shutdownOutput();

        synchronized (readerThread) {
            readerThread.ifPresent(t -> t.interrupt());
        }

        synchronized (writerThread) {
            writerThread.ifPresent(t -> t.interrupt());
        }

        synchronized (isReading) {
            while (isReading.get()) {
                isReading.wait(1000);
                logger.debug("{} still reading", Thread.currentThread()
                        .getName());
            }
        }

        synchronized (isWriting) {
            while (isWriting.get()) {
                isWriting.wait(1000);
                logger.debug("{} still writing", Thread.currentThread()
                        .getName());
            }
        }
        this.getSocket()
                .close();
    }

    @Override
    public Message read() {
        synchronized (readerThread) {
            readerThread = Optional.of(Thread.currentThread());
        }

        try {
            InputStream inputStream = this.getSocket()
                    .getInputStream();

            byte[] sizeBytes = new byte[1000];
            String sizeLineStr = null;

            while (sizeLineStr == null || sizeLineStr.length() == 0) {
                int digit = inputStream.read();

                synchronized (this.isReading) {
                    this.isReading.set(true);
                    this.isReading.notifyAll();
                }

                int i = 0;
                while (digit != '\n' && digit != -1 && i < 1000) {
                    sizeBytes[i] = (byte) digit;
                    i++;
                    digit = inputStream.read();
                }

                synchronized (this.isReading) {
                    this.isReading.set(false);
                    this.isReading.notifyAll();
                }
                sizeLineStr = new String(sizeBytes, 0, i, "utf-8");
            }

            String[] s = sizeLineStr.split(" ");
            logger.info(sizeLineStr);

            int headerLength = Integer.parseInt(s[0]);
            int payloadLength = Integer.parseInt(s[1]);

            byte[] header = new byte[headerLength];
            byte[] payload = new byte[payloadLength];

            inputStream.read(header);
            inputStream.read(payload);

            String messageHeaderStr = new String(header, "utf-8");
            MessageHeader messageHeader = this.getGson()
                    .fromJson(messageHeaderStr, MessageHeader.class);

            long currentTime = System.currentTimeMillis();

            if (messageHeader.getTimestamp() > currentTime) {
                logger.error("Received message from future. Dropping");
                return null;
            }

            // If message is received after 1 min discard
            if (currentTime - messageHeader.getTimestamp() > 60 * 1000) {
                logger.error("Received message too late. Dropping");
                return null;
            }

            String payloadStr = new String(payload, "utf-8");
            return Message.containing(messageHeader, this.getGson()
                    .fromJson(payloadStr, JsonObject.class));
        } catch (Exception e) {
            logger.error("Error", e);
            return null;
        } finally {
            synchronized (this.isReading) {
                this.isReading.set(false);
                this.isReading.notifyAll();
            }
            synchronized (readerThread) {
                readerThread = Optional.empty();
            }
        }
    }

    @Override
    public <T> void send(T data, UUID session) throws IOException {
        synchronized (writerThread) {
            writerThread = Optional.of(Thread.currentThread());
        }

        try {
            OutputStream outputStream = this.getSocket()
                    .getOutputStream();

            synchronized (this.isWriting) {
                this.isWriting.set(true);
                this.isWriting.notifyAll();
            }
            JsonObject obj = new JsonObject(data);

            String msg = this.getGson()
                    .toJson(obj);
            byte[] payload = msg.getBytes("utf-8");

            long timestamp = System.currentTimeMillis();

            MessageHeader messageHeaderObj = new MessageHeader(timestamp, session, getName());

            String msgHeader = this.getGson()
                    .toJson(messageHeaderObj);
            byte[] header = msgHeader.getBytes("utf-8");

            String sizeStr = String.format("%d %d", header.length, payload.length);
            byte[] size = sizeStr.getBytes("utf-8");

            logger.debug("Sending: {}\\n{}{}\\n", sizeStr, msgHeader, msg);

            outputStream.write(size);
            outputStream.write('\n');
            outputStream.write(header);
            outputStream.write(payload);
            outputStream.flush();
        } catch (Exception e) {
            logger.error(e);
        } finally {
            synchronized (this.isWriting) {
                this.isWriting.set(false);
                this.isWriting.notifyAll();
            }
            synchronized (writerThread) {
                writerThread = Optional.empty();
            }
        }
    }

}