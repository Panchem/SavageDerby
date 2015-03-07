package ca.panchem.savagederby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import server.packets.MPlayer;

public class Player extends MPlayer {

    private int charWidth;
    private int charHeight;
    private Texture sprite;

    public Player(int charWidth, int charHeight) {
        this.charWidth = charWidth;
        this.charHeight = charHeight;
        this.sprite = new Texture(Gdx.files.internal("assets/p1_stand.png"));
    }

    public int getCharWidth() {
        return charWidth;
    }

    public void setCharWidth(int charWidth) {
        this.charWidth = charWidth;
    }

    public int getCharHeight() {
        return charHeight;
    }

    public void setCharHeight(int charHeight) {
        this.charHeight = charHeight;
    }

    public Texture getSprite() {
        return sprite;
    }

    public void setSprite(Texture sprite) {
        this.sprite = sprite;
    }
}
