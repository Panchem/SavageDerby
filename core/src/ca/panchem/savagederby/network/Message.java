package ca.panchem.savagederby.network;

public class Message {
    public String message;
    public long createTime;
    public int showTime;
    public int fadeTime;
    public int posY;

    public Message(String message) {
        this.message = message;
        this.createTime = System.currentTimeMillis();
    }

    public float getalpha() {
        int currentLife = (int) (System.currentTimeMillis() - createTime);

        if(currentLife > showTime - fadeTime) {
            return Math.abs(currentLife - showTime) / fadeTime;
        }
        return 1;
    }
}
