package app.saikat.SocketSDK.CommonFiles;

public class Statistics {

    public long startTime;
    public long endTime;

    private long totalMessagesReceived;
    private long totalMessagesSent;

    public Statistics() {
        totalMessagesReceived = 0;
        totalMessagesSent = 0;
    }

    public void threadStarted() {
        startTime = System.currentTimeMillis();
    }

    public void threadStopping() {
        endTime = System.currentTimeMillis();
    }

    public void msgReceived() {
        ++totalMessagesReceived;
    }

    public void msgSent() {
        ++totalMessagesSent;
    }

    public long getTotalMessagesReceived() {
        return totalMessagesReceived;
    }

    public long getTotalMessagesSent() {
        return totalMessagesSent;
    }

}