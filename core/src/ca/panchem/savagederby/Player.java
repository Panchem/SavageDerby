package ca.panchem.savagederby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import server.packets.MPlayer;

public class Player extends MPlayer {

    private String playerType;

    public ResourceLoader res;
    private long lastJumpTime;
    private long jumpTime;
    boolean grounded;
    boolean ceilingd;
    float charXVelocity = 10;
    float charYVelocity = 0;
    float gravityStrength = 12f;
    private String name;

    public Player() {
        res = new ResourceLoader("assets/");
    }

    public void setPlayerType(String playerType) {
        this.playerType = playerType;
    }

    public void update() {
        float moveSpeed = 650;
        float friction = 0.95f;
        float crouchingFriction = 0.99f;
        float airResistance = 0.96f;
        float airMove = 0.4f;
        float playerAcceleration = 30f;

        setCrouching(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT));

        if(ceilingd) {
            charYVelocity -= 10;
        }

        if (!grounded) {
            charYVelocity -= gravityStrength;
            charXVelocity *= airResistance;
        } else if (isCrouching()) {
            charXVelocity *= crouchingFriction;
            charYVelocity = 0;
        } else {
            charYVelocity = 0;
            if (!Gdx.input.isKeyPressed(Input.Keys.A) && !Gdx.input.isKeyPressed(Input.Keys.D))
                charXVelocity *= friction;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && grounded) {
            grounded = false;
            setJumping(true);
            lastJumpTime = System.currentTimeMillis();
            setCrouching(false);
            charYVelocity = 1000;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A) && grounded && !isCrouching() && charXVelocity >= -moveSpeed) {
            charXVelocity -= playerAcceleration;
        } else if (Gdx.input.isKeyPressed(Input.Keys.A) && !isCrouching() && charXVelocity >= -moveSpeed) {
            charXVelocity -= playerAcceleration * airMove;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D) && grounded && !isCrouching() && charXVelocity <= moveSpeed) {
            charXVelocity += playerAcceleration;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D) && !isCrouching() && charXVelocity <= moveSpeed) {
            charXVelocity += playerAcceleration * airMove;
        }

        getPos().y += charYVelocity * Gdx.graphics.getDeltaTime();
        getPos().x += charXVelocity * Gdx.graphics.getDeltaTime();

        if (charXVelocity < -1) {
            direction = false;
            setWalking(charXVelocity < -200);
        } else if (charXVelocity > 1) {
            direction = true;
            setWalking(charXVelocity > 200);
        } else {
            charXVelocity = 0;
            setWalking(false);
        }
        res.updateAnims();
    }

    public void setGrounded(boolean grounded) {
        this.grounded = grounded;
        jumpTime = System.currentTimeMillis() - lastJumpTime;

        if(jumpTime > 10) setJumping(false);
    }

    public float getXVelocity() {
        return charXVelocity;
    }

    public float getYVelocity() {
        return charYVelocity;
    }

    public Vector2 getOPos() {
        if(isWalking()) return new Vector2(getPos().x - res.getAnimationWidth("walk") / 2, getPos().y - res.getAnimationHeight("walk") / 2);
        if(isJumping()) return new Vector2(getPos().x - res.getSpriteWidth("jump") / 2, getPos().y - res.getSpriteHeight("jump") / 2);
        return new Vector2(getPos().x - res.getSpriteWidth("jump") / 2, getPos().y - res.getSpriteHeight("jump") / 2);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCeilingd(boolean ceilingd) {
        this.ceilingd = ceilingd;
    }
}
