package app.saikat.SocketSDK.TestMessageHandlers;

public class TestMessage1 {
    private String str;
    private int i;
    private float f;
    private double d;
    private char c;

    public TestMessage1(String str, int i, float f, double d, char c) {
        this.str = str;
        this.i = i;
        this.f = f;
        this.d = d;
        this.c = c;
    }

    public TestMessage1() {}

    public String getStr() {
        return this.str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public int getI() {
        return this.i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public float getF() {
        return this.f;
    }

    public void setF(float f) {
        this.f = f;
    }

    public double getD() {
        return this.d;
    }

    public void setD(double d) {
        this.d = d;
    }

    public char getC() {
        return this.c;
    }

    public void setC(char c) {
        this.c = c;
    }

}