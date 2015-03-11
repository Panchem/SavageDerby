package server.packets;

import com.badlogic.gdx.math.Vector2;

public class MPlayer {

    protected boolean direction = true;
    protected int playerType;
    boolean walking;
    boolean jumping;
    boolean crouching;
    private Vector2 pos;
    private int id;

    public MPlayer() {
        pos = new Vector2();
    }

    public Vector2 getPos() {
        return pos;
    }

    public void setPos(Vector2 pos) {
        this.pos = pos;
    }

    public void setX(float x) {
        pos.x = x;
    }

    public void setY(float y) {
        pos.y = y;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDirection() {
        if(direction) return 1;
        else return -1;
    }

    public boolean getBDirection() {
        return direction;
    }

    public int getFacing() {
        if(!direction) return 1;
        else return 0;
    }

    public boolean isWalking() {
        return walking;
    }

    public boolean isJumping() {
        return jumping;
    }

    public boolean isCrouching() {
        return crouching;
    }

    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    public void setCrouching(boolean crouching) {
        this.crouching = crouching;
    }

    public void setWalking(boolean walking) {
        this.walking = walking;
    }

    public void setDirection(boolean direction) {
        this.direction = direction;
    }

    public int getPlayerType() {
        return playerType;
    }

    public static class PlayerTypes {
        public static final int GARY = 0;
        public static final int STEVE = 1;
        public static final int BILLY = 2;

        public static String getTypeString(int i) {
            switch (i) {
                case 0: return "Gary";
                case 1: return "Steve";
                case 2: return "Billy";
                default: return "NOT A REAL PLAYER";
            }
        }
    }

    public void setPlayerType(int playerType) {
        this.playerType = playerType;
    }
}
