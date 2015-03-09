package ca.panchem.savagederby.network;

import com.badlogic.gdx.math.Vector2;
import server.packets.MPlayer;

public class RichMPlayer extends MPlayer {
    int direction;
    int facing;
    boolean walking;
    boolean jumping;
    String playerType;
    String name;
    int xVel, yVel;

    public RichMPlayer(Vector2 pos, int id, String name, String playerType) {
        super(pos, id, name);
        this.playerType = playerType;
    }

    public RichMPlayer() {
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public boolean isWalking() {
        return walking;
    }

    public void setWalking(boolean walking) {
        this.walking = walking;
    }

    public boolean isJumping() {
        return jumping;
    }

    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    public String getPlayerType() {
        return playerType;
    }

    public void setPlayerType(String playerType) {
        this.playerType = playerType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFacing() {
        return facing;
    }

    public void setFacing(int facing) {
        this.facing = facing;
    }
}
