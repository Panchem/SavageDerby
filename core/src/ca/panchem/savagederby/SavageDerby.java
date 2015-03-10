/**
 * Savage Derby main game class
 * Deals with actual gameplay, players, and physics
 *
 * @author Panchem
 */

package ca.panchem.savagederby;

//Imports
import ca.panchem.savagederby.network.Message;
import ca.panchem.savagederby.network.NetworkManager;
import ca.panchem.savagederby.players.PlayerOne;
import ca.panchem.savagederby.players.PlayerTwo;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser;
import server.packets.MPlayer;
import javax.swing.*;

public class SavageDerby extends ApplicationAdapter {

    //Rendering
	SpriteBatch spriteBatch;
    SpriteBatch guiBatch;
    BitmapFont font;
    OrthographicCamera camera;
    TiledMap tiledMap;
    OrthoCachedTiledMapRenderer tiledMapRenderer;

    //Physics
    Box2DMapObjectParser parser;
    World world;
    Box2DDebugRenderer b2dr;

    //Network
    NetworkManager networkManager;

    public static final int mapWidth = 2800;
    public static final int mapHeight = 2100;

    //Client & Networking
    PlayerOne player;
    PlayerTwo oP;
    private Body playerBody;
    public static ChatBox chatBox;

    /**
     * Called on first program run
     */
    @Override
	public void create () {
        parser = new Box2DMapObjectParser();
        world = new World(new Vector2(0f, 0f), false);

        /** Custom Box2D Contact listener */
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture fa = contact.getFixtureA();
                Fixture fb = contact.getFixtureB();

                if(fb.getUserData() != null && fb.getUserData().equals("groundSensor")) {
                    player.setGrounded(true);
                } else if(fa.getUserData() != null && fa.getUserData().equals("groundSensor")) {
                    player.setGrounded(true);
                }

                if(fb.getUserData() != null && fb.getUserData().equals("headSensor")) {
                    player.setCeilingd(true);
                } else if(fa.getUserData() != null && fa.getUserData().equals("headSensor")) {
                    player.setCeilingd(true);
                }
            }

            @Override
            public void endContact(Contact contact) {
                Fixture fa = contact.getFixtureA();
                Fixture fb = contact.getFixtureB();

                if(fb.getUserData() != null && fb.getUserData().equals("groundSensor")) {
                    player.setGrounded(false);
                } else if(fa.getUserData() != null && fa.getUserData().equals("groundSensor")) {
                    player.setGrounded(false);
                }

                if(fb.getUserData() != null && fb.getUserData().equals("headSensor")) {
                    player.setCeilingd(false);
                } else if(fa.getUserData() != null && fa.getUserData().equals("headSensor")) {
                    player.setCeilingd(false);
                }
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
        b2dr = new Box2DDebugRenderer();
        font = new BitmapFont();
		spriteBatch = new SpriteBatch();
        guiBatch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        tiledMap = new TmxMapLoader().load("assets/maps/bubblyMap.tmx");
        player = new PlayerOne();
        player.setPos(new Vector2(350, 350));
        oP = new PlayerTwo();
        tiledMapRenderer = new OrthoCachedTiledMapRenderer(tiledMap);
        tiledMapRenderer.setBlending(true);
        chatBox = new ChatBox();

        createPlayerBox();

        player.setName(JOptionPane.showInputDialog(null, "Input a character name"));
        String ip = JOptionPane.showInputDialog(null, "IP");

        networkManager = new NetworkManager(player.getPos());
        networkManager.connect(ip);
        networkManager.login(player.getName());

        parser.load(world, tiledMap);
	}

    /**
     * Called every frame
     * Renders the image
     * Updates player positions & physics
     */
    @Override
	public void render () {
        //Clear the screen with a black background
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        //Render the map before anything else
        tiledMapRenderer.render();

        //Box2D debug rendering
        //b2dr.render(world, camera.combined);

        //Begin sprite drawing
		spriteBatch.begin();

        //Render other players
        if(networkManager.isConnected()) {

            //Set the network manager busy so it wont update the player array while its being rendered
            networkManager.setBusy(true);

            //For each other player on the server
            for (MPlayer p : networkManager.getPlayers()) {

                if(p.isJumping()) {
                    spriteBatch.draw(oP.res.getSprite("jump"),
                            p.getPos().x - oP.res.getSpriteWidth("jump") / 2 + oP.res.getSpriteWidth("jump") * p.getFacing(),
                            p.getPos().y - oP.res.getSpriteHeight("jump") / 2,
                            oP.res.getSpriteWidth("jump") * p.getDirection(),
                            oP.res.getSpriteHeight("jump"));

                } else if(p.isCrouching()) {
                    spriteBatch.draw(oP.res.getSprite("crouch"),
                            p.getPos().x - oP.res.getSpriteWidth("crouch") / 2 + oP.res.getSpriteWidth("crouch") * p.getFacing(),
                            p.getPos().y - oP.res.getSpriteHeight("jump") / 2,
                            oP.res.getSpriteWidth("crouch") * p.getDirection(),
                            oP.res.getSpriteHeight("crouch"));

                } else if(p.isWalking()) {
                    spriteBatch.draw(oP.res.getAnimation("walk").getTexture(),
                            p.getPos().x - oP.res.getAnimationWidth("walk") / 2 + oP.res.getAnimationWidth("walk") * p.getFacing(),
                            p.getPos().y - oP.res.getAnimationHeight("walk") / 2,
                            oP.res.getAnimationWidth("walk") * p.getDirection(),
                            oP.res.getAnimationHeight("walk"));

                } else {
                    spriteBatch.draw(oP.res.getSprite("stand"),
                            p.getPos().x - oP.res.getSpriteWidth("stand") / 2 + oP.res.getSpriteWidth("stand") * p.getFacing(),
                            p.getPos().y - oP.res.getSpriteHeight("stand") / 2,
                            oP.res.getSpriteWidth("stand") * p.getDirection(),
                            oP.res.getSpriteHeight("stand"));
                }

                //Render the name above the players head
                try {
                    String pName = networkManager.staticData.getName(p.getId());
                    font.draw(spriteBatch,
                            pName,
                            p.getPos().x - pName.length() * font.getSpaceWidth() / 2,
                            p.getPos().y + oP.res.getSpriteWidth("stand") / 2 + font.getLineHeight());
                } catch (NullPointerException e) {
                    System.out.print("");
                }

            }

            //The array is done being rendered so the NetworkManager can update the array again
            networkManager.setBusy(false);
        }

        //Render local player
        if(player.isJumping()) {
            spriteBatch.draw(player.res.getSprite("jump"),
                    player.getOPos().x + player.res.getSpriteWidth("jump") * player.getFacing(),
                    player.getOPos().y,
                    player.res.getSpriteWidth("jump") * player.getDirection(),
                    player.res.getSpriteHeight("jump"));

        } else if(player.isCrouching()) {
            spriteBatch.draw(player.res.getSprite("crouch"),
                    player.getOPos().x + player.res.getSpriteWidth("crouch") * player.getFacing(),
                    player.getOPos().y,
                    player.res.getSpriteWidth("crouch") * player.getDirection(),
                    player.res.getSpriteHeight("crouch"));

        } else if(player.isWalking()) {
            spriteBatch.draw(player.res.getAnimation("walk").getTexture(),
                    player.getOPos().x + player.res.getAnimationWidth("walk") * player.getFacing(),
                    player.getOPos().y,
                    player.res.getAnimationWidth("walk") * player.getDirection(),
                    player.res.getAnimationHeight("walk"));

        } else {
            spriteBatch.draw(player.res.getSprite("stand"),
                    player.getOPos().x + player.res.getSpriteWidth("jump") * player.getFacing(),
                    player.getOPos().y,
                    player.res.getSpriteWidth("stand") * player.getDirection(),
                    player.res.getSpriteHeight("stand"));
        }

        //Draw the name above the players head
        font.draw(spriteBatch,
                player.getName(),
                player.getPos().x - player.getName().length() * font.getSpaceWidth() / 2,
                player.getPos().y + player.res.getSpriteHeight("stand") / 2 + font.getLineHeight());

        //Done rendering the sprites
        spriteBatch.end();

        //Update the chat box
        chatBox.update();

        //Begin rendering UI elements
        guiBatch.begin();
        font.draw(guiBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 720 - 10);
        font.draw(guiBatch, "Jumping: " + player.isJumping(), 10, 720 - 30);
        font.draw(guiBatch, "Gravity: " + player.getYVelocity(), 10, 720 - 50);
        font.draw(guiBatch, "xVelocity: " + player.getXVelocity(), 10, 720 - 70);

        //Fancy colouring for the ping
        if(networkManager.getPing() < 75) font.setColor(Color.GREEN);
        else if(networkManager.getPing() < 100) font.setColor(Color.YELLOW);
        else font.setColor(Color.RED);
        font.draw(guiBatch, "Ping: " + networkManager.getSPing(), 10, 720 - 90);
        font.setColor(Color.WHITE);

        //Draw all messages in the ChatBox
        for (Message m : chatBox.getMessages()) {
            font.draw(guiBatch, m.message, 10, 20 + m.posY);
        }

        //Finish rendering the UI elements
        guiBatch.end();

        /** PREPARE FOR NEXT FRAME */

        player.update();
        oP.res.updateAnims();

        //Set the Box2D box to the player position
        playerBody.setTransform(player.getPos(), 0);

        //Do collision calculations
        world.step(144 * Gdx.graphics.getDeltaTime(), 30, 20);

        //Update player position
        player.setPos(playerBody.getPosition());

        //Update camera position
        camera.position.set(player.getPos(), 0);
        cameraBoundsCheck();
        camera.update();

        spriteBatch.setProjectionMatrix(camera.combined);
        tiledMapRenderer.setView(camera);
        networkManager.update(player);
	}

    /** Make sure the camera is properly positioned */
    private void cameraBoundsCheck() {
        if(camera.position.x < camera.viewportWidth / 2) camera.position.x = camera.viewportWidth / 2;
        if(camera.position.y < camera.viewportHeight / 2) camera.position.y = camera.viewportHeight / 2;
        if(camera.position.x > mapWidth - camera.viewportWidth / 2) camera.position.x = mapWidth - camera.viewportWidth / 2;
        if(camera.position.y > mapHeight - camera.viewportHeight / 2) camera.position.y = mapHeight - camera.viewportHeight / 2;
    }

    private void createPlayerBox() {
        BodyDef playerDef = new BodyDef();
        playerDef.type = BodyDef.BodyType.DynamicBody;
        playerDef.position.set(300 , 300);

        PolygonShape playerShape = new PolygonShape();
        playerShape.setAsBox(20f, 60f / 2f);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(18f);
        circleShape.setPosition(new Vector2(0, -28));

        CircleShape sensorCircle = new CircleShape();
        sensorCircle.setRadius(6f);
        sensorCircle.setPosition(new Vector2(0, -43f));

        CircleShape upperSensorCircle = new CircleShape();
        upperSensorCircle.setRadius(6f);
        upperSensorCircle.setPosition(new Vector2(0, 32f));

        FixtureDef groundSensor = new FixtureDef();
        groundSensor.isSensor = true;
        groundSensor.shape = sensorCircle;

        FixtureDef headSensor = new FixtureDef();
        headSensor.isSensor = true;
        headSensor.shape = upperSensorCircle;

        FixtureDef playerFixtureDef = new FixtureDef();
        playerFixtureDef.shape = playerShape;

        FixtureDef playerBall = new FixtureDef();
        playerBall.shape = circleShape;

        playerBody = world.createBody(playerDef);
        playerBody.createFixture(playerFixtureDef);
        playerBody.createFixture(playerBall);
        playerBody.createFixture(groundSensor).setUserData("groundSensor");
        playerBody.createFixture(headSensor).setUserData("headSensor");
    }
}
