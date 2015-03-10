package ca.panchem.savagederby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.dermetfan.gdx.graphics.g2d.AnimatedSprite;

import java.util.HashMap;

public class ResourceLoader {

    private HashMap<String, Texture> sprites;
    private HashMap<String, AnimatedSprite> animations;
    private String path;

    public ResourceLoader(String path) {
        sprites = new HashMap<>();
        animations = new HashMap<>();
        this.path = path;
    }

    public void load(String path, String key) {
        sprites.put(key, new Texture(Gdx.files.internal(this.path + path)));
    }

    public void load(String key, float frameTime, TextureRegion... frames) {
        animations.put(key, new AnimatedSprite(new Animation(frameTime, frames)));
        animations.get(key).setAutoUpdate(true);
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Texture getSprite(String key) {
        return sprites.get(key);
    }

    public int getSpriteWidth(String key) {
        return sprites.get(key).getWidth();
    }

    public int getSpriteHeight(String key) {
        return sprites.get(key).getHeight();
    }

    public int getAnimationWidth(String key) {
        return (int) animations.get(key).getWidth();
    }

    public int getAnimationHeight(String key) {
        return (int) animations.get(key).getHeight();
    }

    public AnimatedSprite getAnimation(String key) {
        return animations.get(key);
    }

    public String getPath() {
        return path;
    }

    public void updateAnims() {
        for (AnimatedSprite a : animations.values()) {
            a.update(Gdx.graphics.getDeltaTime());
        }
    }
}
