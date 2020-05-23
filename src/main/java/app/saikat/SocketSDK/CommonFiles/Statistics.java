package app.saikat.SocketSDK.CommonFiles;

public class Statistics {

	private long startTime;
	private long endTime;

	private long totalMessagesReceived;
	private long totalMessagesSent;

	public Statistics() {
		totalMessagesReceived = 0;
		totalMessagesSent = 0;
		startTime = 0;
		endTime = 0;
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

	// Getters
	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public long getTotalMessagesReceived() {
		return totalMessagesReceived;
	}

	public long getTotalMessagesSent() {
		return totalMessagesSent;
	}

}