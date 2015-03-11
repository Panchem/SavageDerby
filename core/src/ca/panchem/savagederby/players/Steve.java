package ca.panchem.savagederby.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Steve extends Player {

    public Steve() {
        super();

        setPlayerType(PlayerTypes.STEVE);
        res.setPath(res.getPath() + "p1/");

        res.load("stand", "p1_stand.png");
        res.load("jump", "p1_jump.png");
        res.load("hurt", "p1_hurt.png");
        res.load("crouch", "p1_duck.png");

        res.load("walk", 0.05f,
                new TextureRegion(new Texture(Gdx.files.internal("assets/p1/walk/p1_walk01.png"))),
                new TextureRegion(new Texture(Gdx.files.internal("assets/p1/walk/p1_walk02.png"))),
                new TextureRegion(new Texture(Gdx.files.internal("assets/p1/walk/p1_walk03.png"))),
                new TextureRegion(new Texture(Gdx.files.internal("assets/p1/walk/p1_walk04.png"))),
                new TextureRegion(new Texture(Gdx.files.internal("assets/p1/walk/p1_walk05.png"))),
                new TextureRegion(new Texture(Gdx.files.internal("assets/p1/walk/p1_walk06.png"))),
                new TextureRegion(new Texture(Gdx.files.internal("assets/p1/walk/p1_walk07.png"))),
                new TextureRegion(new Texture(Gdx.files.internal("assets/p1/walk/p1_walk08.png"))),
                new TextureRegion(new Texture(Gdx.files.internal("assets/p1/walk/p1_walk09.png"))),
                new TextureRegion(new Texture(Gdx.files.internal("assets/p1/walk/p1_walk10.png"))),
                new TextureRegion(new Texture(Gdx.files.internal("assets/p1/walk/p1_walk11.png"))));
        res.getAnimation("walk").getAnimation().setPlayMode(Animation.PlayMode.LOOP);
    }
}
