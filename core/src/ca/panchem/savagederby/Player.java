package ca.panchem.savagederby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import server.packets.MPlayer;

public class Player extends MPlayer {

    private String playerType;
    public ResourceLoader res;
    boolean grounded;
    boolean walking;
    boolean jumping;
    boolean crouching;
    float charXVelocity = 0;
    float charYVelocity = 0;
    float gravityStrength = 12f;
    private int direction = 1;
    private int facing;
    private String name;

    public Player() {
        res = new ResourceLoader("assets/");
    }

    public String getPlayerType() {
        return playerType;
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

        crouching = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT);

        if(!grounded) {
            charYVelocity -= gravityStrength;
            charXVelocity *= airResistance;
        } else if(crouching) {
            charXVelocity *= crouchingFriction;
        } else {
            charYVelocity = 0;
            if(!Gdx.input.isKeyPressed(Input.Keys.A) && !Gdx.input.isKeyPressed(Input.Keys.D))charXVelocity *= friction;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.SPACE) && grounded)  {
            grounded = false;
            jumping = true;
            crouching = false;
            charYVelocity = 1000;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.A) && grounded && !crouching && charXVelocity >= -moveSpeed) {
            charXVelocity -= playerAcceleration;
        } else if(Gdx.input.isKeyPressed(Input.Keys.A) && !crouching && charXVelocity >= -moveSpeed){
            charXVelocity -= playerAcceleration * airMove;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.D) && grounded && !crouching && charXVelocity <= moveSpeed) {
            charXVelocity += playerAcceleration;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D) && !crouching && charXVelocity <= moveSpeed) {
            charXVelocity += playerAcceleration * airMove;
        }

        getPos().y += charYVelocity * Gdx.graphics.getDeltaTime();
        getPos().x += charXVelocity * Gdx.graphics.getDeltaTime();

        if(charXVelocity < -1) {
            facing = res.getSpriteWidth("stand");
            direction = -1;
            walking = charXVelocity < -200;
        } else if(charXVelocity > 1) {
            direction = 1;
            facing = 0;
            walking = charXVelocity > 200;
        } else {
            charXVelocity = 0;
            walking = false;
        }
        res.updateAnims();
    }

    public int getDirection() {
        return direction;
    }

    public boolean isGrounded() {
        return grounded;
    }

    public void setGrounded(boolean grounded) {
        this.grounded = grounded;
        jumping = false;
    }

    public float getXVelocity() {
        return charXVelocity;
    }

    public float getYVelocity() {
        return charYVelocity;
    }

    public int getFacing() {
        return facing;
    }

    public boolean isWalking() {
        return walking;
    }

    public boolean isJumping() {
        return jumping;
    }

    public Vector2 getOPos() {
        if(walking) return new Vector2(getPos().x - res.getAnimationWidth("walk") / 2, getPos().y - res.getAnimationHeight("walk") / 2);
        if(jumping) return new Vector2(getPos().x - res.getSpriteWidth("jump") / 2, getPos().y - res.getSpriteHeight("jump") / 2);
        return new Vector2(getPos().x - res.getSpriteWidth("jump") / 2, getPos().y - res.getSpriteHeight("jump") / 2);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCrouching() {
        return crouching;
    }
}
