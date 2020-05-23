package app.saikat.SocketSDK.CommonFiles;

/**
 * To pass internal information to client
 */
public class InfoMessage {

	private String title;
	private String message;


	public InfoMessage() {
	}

	public InfoMessage(String title, String message) {
		this.title = title;
		this.message = message;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public InfoMessage title(String title) {
		this.title = title;
		return this;
	}

	public InfoMessage message(String message) {
		this.message = message;
		return this;
	}

	@Override
	public String toString() {
		return "{" +
			"title='" + getTitle() + "'" +
			", message='" + getMessage() + "'" +
			"}";
	}

}