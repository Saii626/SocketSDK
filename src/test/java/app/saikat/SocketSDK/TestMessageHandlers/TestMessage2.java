package app.saikat.SocketSDK.TestMessageHandlers;

public class TestMessage2 {

	private Class<?> cls;
	private String str;


	public Class<?> getCls() {
		return this.cls;
	}

	public void setCls(Class<?> cls) {
		this.cls = cls;
	}

	public String getStr() {
		return this.str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public TestMessage2(Class<?> cls, String str) {
		this.cls = cls;
		this.str = str;
	}

	public TestMessage2() {}
}