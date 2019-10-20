// package app.saikat.SocketSDK.IO;

// import java.io.InputStream;
// import java.net.SocketException;
// import java.security.MessageDigest;
// import java.security.NoSuchAlgorithmException;
// import java.util.Arrays;
// import java.util.concurrent.atomic.AtomicBoolean;

// import com.google.gson.Gson;

// import app.saikat.LogManagement.Logger;
// import app.saikat.LogManagement.LoggerFactory;

// import app.saikat.GsonManagement.JsonObject;
// import app.saikat.SocketSDK.CommonFiles.MessageHeader;
// import app.saikat.SocketSDK.Exceptions.SocketClosedException;

// public class Receiver {

//     private Logger logger = LoggerFactory.getLogger(this.getClass());
//     private final Gson gson;
//     private final InputStream inputStream;

//     private AtomicBoolean isReading;

//     public Receiver(Gson gson, InputStream inputStream) {
//         this.gson = gson;
//         this.inputStream = inputStream;
//         this.isReading = new AtomicBoolean();
//     }

//     public void shutdown() throws InterruptedException {

//         // Wait for current packet being read
//         // isReading is atomic boolean. Getting monitor on "isReading" so than we can wait on it
//         synchronized (isReading) {
//             while (isReading.get()) {
//                 isReading.wait(1000);
//                 logger.warn("{} still reading", Thread.currentThread()
//                         .getName());
//             }
//         }
//     }

//     public synchronized Message read() throws SocketClosedException {
//         // try {
//         //     Thread.sleep(200);
//         // } catch (InterruptedException e) {
//         //     // TODO Auto-generated catch block
//         //     e.printStackTrace();
//         // }
//         // return null;
//         try {

//             byte[] sizeBytes = new byte[1000];
//             String sizeLineStr = null;

//             // while (sizeLineStr == null || sizeLineStr.length() == 0) {
//             //     this.inputStream.re
//             // }

//             while (sizeLineStr == null || sizeLineStr.length() == 0) {
//                 int digit = this.inputStream.read();
//                 isReading.set(true);

//                 int i = 0;
//                 while (digit != '\n' && digit != -1 && i < 1000) {
//                     sizeBytes[i] = (byte) digit;
//                     i++;
//                     digit = this.inputStream.read();
//                 }

//                 sizeLineStr = new String(sizeBytes, 0, i, "utf-8");
//             }

//             String[] s = sizeLineStr.split(" ");

//             int headerLength = Integer.parseInt(s[0]);
//             int payloadLength = Integer.parseInt(s[1]);

//             byte[] header = new byte[headerLength];
//             byte[] payload = new byte[payloadLength];

//             this.inputStream.read(header);
//             this.inputStream.read(payload);

//             String messageHeaderStr = new String(header, "utf-8");
//             MessageHeader messageHeader = this.gson.fromJson(messageHeaderStr, MessageHeader.class);

//             long currentTime = System.currentTimeMillis();

//             if (messageHeader.getTimestamp() > currentTime) {
//                 logger.error("Received message from future. Dropping");
//                 return null;
//             }

//             // If message is received after 1 min discard
//             if (currentTime - messageHeader.getTimestamp() > 60 * 1000) {
//                 logger.error("Received message too late. Dropping");
//                 return null;
//             }

//             MessageDigest messageDigest;
//             try {
//                 messageDigest = MessageDigest.getInstance("SHA-256");
//             } catch (NoSuchAlgorithmException e) {
//                 logger.error("Error: {}", e);
//                 return null;
//             }

//             byte[] digest = messageDigest.digest(payload);

//             if (!Arrays.equals(messageHeader.getMsgDigest(), digest)) {
//                 logger.error("Payload modified. Dropping");
//                 return null;
//             }

//             String payloadStr = new String(payload, "utf-8");
//             return Message.containing(messageHeader, this.gson.fromJson(payloadStr, JsonObject.class));
//         } catch (SocketException e) {
//             throw new SocketClosedException();
//         } catch (Exception e) {
//             logger.error("Error", e);
//             return null;
//         } finally {
//             isReading.set(false);
//         }
//     }
// }
