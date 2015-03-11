package ca.panchem.savagederby.players;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Gary extends Player {

    public Gary() {
        super();

        setPlayerType(PlayerTypes.GARY);
        res.setPath(res.getPath() + "p2/");

        res.load("stand", "p2_stand.png");
        res.load("jump", "p2_jump.png");
        res.load("hurt", "p2_hurt.png");
        res.load("crouch", "p2_duck.png");

        res.load("walk", 0.05f,
                new TextureRegion(new Texture(Gdx.files.internal("assets/p2/walk/p2_walk01.png"))),
                new TextureRegion(new Texture(Gdx.files.internal("assets/p2/walk/p2_walk02.png"))),
                new TextureRegion(new Texture(Gdx.files.internal("assets/p2/walk/p2_walk03.png"))),
                new TextureRegion(new Texture(Gdx.files.internal("assets/p2/walk/p2_walk04.png"))),
                new TextureRegion(new Texture(Gdx.files.internal("assets/p2/walk/p2_walk05.png"))),
                new TextureRegion(new Texture(Gdx.files.internal("assets/p2/walk/p2_walk06.png"))),
                new TextureRegion(new Texture(Gdx.files.internal("assets/p2/walk/p2_walk07.png"))),
                new TextureRegion(new Texture(Gdx.files.internal("assets/p2/walk/p2_walk08.png"))),
                new TextureRegion(new Texture(Gdx.files.internal("assets/p2/walk/p2_walk09.png"))),
                new TextureRegion(new Texture(Gdx.files.internal("assets/p2/walk/p2_walk10.png"))),
                new TextureRegion(new Texture(Gdx.files.internal("assets/p2/walk/p2_walk11.png"))));

        res.getAnimation("walk").getAnimation().setPlayMode(Animation.PlayMode.LOOP);
    }
}
