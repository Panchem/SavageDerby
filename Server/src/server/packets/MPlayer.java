package server.packets;

import com.badlogic.gdx.math.Vector2;

public class MPlayer {

    protected boolean direction = true;
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
}
