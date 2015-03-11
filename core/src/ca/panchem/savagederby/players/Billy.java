package ca.panchem.savagederby.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Billy extends Player {

    public Billy() {
        super();

        setPlayerType(PlayerTypes.BILLY);
        res.setPath(res.getPath() + "p3/");

        res.load("stand",  "p3_stand.png");
        res.load("jump",   "p3_jump.png");
        res.load("hurt",   "p3_hurt.png");
        res.load("crouch", "p3_duck.png");

        res.load("walk", 0.05f,
                new TextureRegion(new Texture(Gdx.files.internal("assets/p3/walk/p3_walk01.png"))),
                new TextureRegion(new Texture(Gdx.files.internal("assets/p3/walk/p3_walk02.png"))),
                new TextureRegion(new Texture(Gdx.files.internal("assets/p3/walk/p3_walk03.png"))),
                new TextureRegion(new Texture(Gdx.files.internal("assets/p3/walk/p3_walk04.png"))),
                new TextureRegion(new Texture(Gdx.files.internal("assets/p3/walk/p3_walk05.png"))),
                new TextureRegion(new Texture(Gdx.files.internal("assets/p3/walk/p3_walk06.png"))),
                new TextureRegion(new Texture(Gdx.files.internal("assets/p3/walk/p3_walk07.png"))),
                new TextureRegion(new Texture(Gdx.files.internal("assets/p3/walk/p3_walk08.png"))),
                new TextureRegion(new Texture(Gdx.files.internal("assets/p3/walk/p3_walk09.png"))),
                new TextureRegion(new Texture(Gdx.files.internal("assets/p3/walk/p3_walk10.png"))),
                new TextureRegion(new Texture(Gdx.files.internal("assets/p3/walk/p3_walk11.png"))));

        res.getAnimation("walk").getAnimation().setPlayMode(Animation.PlayMode.LOOP);
    }
}