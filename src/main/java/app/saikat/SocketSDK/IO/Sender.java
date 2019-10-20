// package app.saikat.SocketSDK.IO;

// import java.io.IOException;
// import java.io.OutputStream;
// import java.security.MessageDigest;
// import java.security.NoSuchAlgorithmException;
// import java.util.UUID;
// import java.util.concurrent.atomic.AtomicBoolean;

// import com.google.gson.Gson;

// import app.saikat.LogManagement.Logger;
// import app.saikat.LogManagement.LoggerFactory;

// import app.saikat.GsonManagement.JsonObject;
// import app.saikat.SocketSDK.CommonFiles.MessageHeader;

// public class Sender {

//     private Logger logger = LoggerFactory.getLogger(this.getClass());
//     private final OutputStream outputStream;
//     private final Gson gson;

//     private AtomicBoolean isSending;

//     public Sender(Gson gson, OutputStream outputStream) {
//         this.gson = gson;
//         this.outputStream = outputStream;
//         this.isSending = new AtomicBoolean();
//     }

//     public void shutdown() throws InterruptedException {
//         synchronized (isSending) {
//             while (isSending.get()) {
//                 isSending.wait(1000);
//                 logger.warn("{} still sending message", Thread.currentThread()
//                         .getName());
//             }
//         }
//     }

//     public synchronized <T> void send(T object, UUID session) throws IOException {
//         // try {
//         //     Thread.sleep(200);
//         // } catch (InterruptedException e) {
//         //     // TODO Auto-generated catch block
//         //     e.printStackTrace();
//         // }
//         isSending.set(true);

//         try {
//             JsonObject obj = new JsonObject(object);

//             String msg = this.gson.toJson(obj);
//             byte[] payload = msg.getBytes("utf-8");
            
//             MessageDigest messageDigest;
//             try {
//                 messageDigest = MessageDigest.getInstance("SHA-256");
//             } catch (NoSuchAlgorithmException e) {
//                 logger.error("Error: {}", e);
//                 return;
//             }

//             byte[] digest = messageDigest.digest(payload);
//             long timestamp = System.currentTimeMillis();

//             MessageHeader messageHeaderObj = new MessageHeader(timestamp, session, digest, name);

//             String msgHeader = this.gson.toJson(messageHeaderObj);
//             byte[] header = msgHeader.getBytes("utf-8");

//             String sizeStr = String.format("%d %d", header.length, payload.length);
//             byte[] size = sizeStr.getBytes("utf-8");

//             logger.debug("Sending: {}\\n{}{}\\n", sizeStr, msgHeader, msg);

//             this.outputStream.write(size);
//             this.outputStream.write('\n');
//             this.outputStream.write(header);
//             this.outputStream.write(payload);
//             this.outputStream.write('\n');
//             this.outputStream.flush();
//         }catch (Exception e) {
//             logger.error(e);
//         } finally {
//             isSending.set(false);
//         }
//     }

//     public <T> void send(T object) throws NoSuchAlgorithmException, IOException {
//         this.send(object, UUID.randomUUID());
//     }
// }
