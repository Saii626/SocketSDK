package app.saikat.SocketSDK.TestMessageHandlers;

public class TestMessage3 {

    private TestMessage1 msg1;
    private TestMessage2 msg2;
    private int val;

    public TestMessage3(TestMessage1 msg1, TestMessage2 msg2, int val) {
        this.msg1 = msg1;
        this.msg2 = msg2;
        this.val = val;
    }

    public TestMessage3() {}

    public TestMessage1 getMsg1() {
        return this.msg1;
    }

    public void setMsg1(TestMessage1 msg1) {
        this.msg1 = msg1;
    }

    public TestMessage2 getMsg2() {
        return this.msg2;
    }

    public void setMsg2(TestMessage2 msg2) {
        this.msg2 = msg2;
    }

    public int getVal() {
        return this.val;
    }

    public void setVal(int val) {
        this.val = val;
    }

}