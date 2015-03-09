package server.packets;

import com.badlogic.gdx.math.Vector2;

public class MPlayer {

    private Vector2 pos;
    private int id;


    public MPlayer(Vector2 pos, int id, String name) {
        this.pos = pos;
        this.id = id;
    }

    public MPlayer() {
    }

    public Vector2 getPos() {
        return pos;
    }

    public void setPos(Vector2 pos) {
        this.pos = pos;
    }

    public void setX(int x) {
        pos.x = x;
    }

    public void setY(int y) {
        pos.y = y;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
