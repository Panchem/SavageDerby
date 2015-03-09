package ca.panchem.savagederby;

import ca.panchem.savagederby.network.Message;
import ca.panchem.savagederby.network.NetworkManager;
import ca.panchem.savagederby.players.PlayerOne;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
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
    ResourceLoader res;
    BitmapFont font;
    OrthographicCamera camera;
    TiledMap tiledMap;
    OrthoCachedTiledMapRenderer tiledMapRenderer;
    Box2DMapObjectParser parser;
    World world;
    Box2DDebugRenderer b2dr;
    NetworkManager networkManager;

    public static final int mapWidth = 2800;
    public static final int mapHeight = 2100;

    //Client & Networking
    PlayerOne player;
    private Body playerBody;
    public static ChatBox chatBox;

    @Override
	public void create () {
        res = new ResourceLoader();
        parser = new Box2DMapObjectParser();
        world = new World(new Vector2(0f, 0f), false);
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
        res.setPath("assets/p1/");
        res.load("p2_stand.png", "p2");
        res.load("p1_stand.png", "p1");
	}

    private void createPlayerBox() {
        BodyDef playerDef = new BodyDef();
        playerDef.type = BodyDef.BodyType.DynamicBody;
        playerDef.position.set(300 , 300);

        PolygonShape playerShape = new PolygonShape();
        playerShape.setAsBox(53f / 2f, 60f / 2f);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(18f);
        circleShape.setPosition(new Vector2(0, -28));

        CircleShape sensorCircle = new CircleShape();
        sensorCircle.setRadius(5f);
        sensorCircle.setPosition(new Vector2(0, -43f));

        FixtureDef groundSensor = new FixtureDef();
        groundSensor.isSensor = true;
        groundSensor.shape = sensorCircle;

        FixtureDef playerFixtureDef = new FixtureDef();
        playerFixtureDef.shape = playerShape;

        FixtureDef playerBall = new FixtureDef();
        playerBall.shape = circleShape;

        playerBody = world.createBody(playerDef);
        playerBody.createFixture(playerFixtureDef);
        playerBody.createFixture(playerBall);
        playerBody.createFixture(groundSensor).setUserData("groundSensor");
    }

    @Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        tiledMapRenderer.render();
        //b2dr.render(world, camera.combined);

        //Begin sprite drawing
		spriteBatch.begin();

        if(networkManager.isConnected()) {
            networkManager.setBusy(true);
            for (MPlayer p : networkManager.getPlayers()) {
                spriteBatch.draw(res.getSprite("p2"),
                        p.getPos().x - res.getSpriteWidth("p2") / 2,
                        p.getPos().y - res.getSpriteHeight("p2") / 2,
                        res.getSpriteWidth("p2"),
                        res.getSpriteHeight("p2"));

                try {
                    String pName = networkManager.staticData.getName(p.getId());
                    font.draw(spriteBatch,
                            pName,
                            p.getPos().x - pName.length() * font.getSpaceWidth() / 2,
                            p.getPos().y + res.getSpriteWidth("p2") / 2 + font.getLineHeight());
                } catch (NullPointerException e) {
                    System.out.print("");
                }

            }
            networkManager.setBusy(false);
        }

        if(player.isCrouching()) {
            spriteBatch.draw(player.res.getSprite("crouch"),
                    player.getOPos().x + player.getFacing(),
                    player.getOPos().y,
                    player.res.getSpriteWidth("crouch") * player.getDirection(),
                    player.res.getSpriteHeight("crouch"));

        } else if(player.isWalking()) {
            spriteBatch.draw(player.res.getAnimation("walk").getTexture(),
                    player.getOPos().x + player.getFacing(),
                    player.getOPos().y,
                    player.res.getAnimationWidth("walk") * player.getDirection(),
                    player.res.getAnimationHeight("walk"));

        } else {
            spriteBatch.draw(res.getSprite("p1"),
                    player.getPos().x - res.getSpriteWidth("p1") / 2 + player.getFacing(),
                    player.getPos().y - res.getSpriteHeight("p1") / 2,
                    res.getSpriteWidth("p1") * player.getDirection(),
                    res.getSpriteHeight("p1"));
        }

        font.draw(spriteBatch,
                player.getName(),
                player.getPos().x - player.getName().length() * font.getSpaceWidth() / 2,
                player.getPos().y + res.getSpriteHeight("p1") / 2 + font.getLineHeight());

        spriteBatch.end();

        chatBox.update();

        guiBatch.begin();
        font.draw(guiBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 720 - 10);
        font.draw(guiBatch, "Grounded: " + player.isGrounded(), 10, 720 - 30);
        font.draw(guiBatch, "Gravity: " + player.getYVelocity(), 10, 720 - 50);
        font.draw(guiBatch, "xVelocity: " + player.getXVelocity(), 10, 720 - 70);
        font.draw(guiBatch, "Ping: " + networkManager.getPing(), 10, 720 - 90);

        for (Message m : chatBox.getMessages()) {
            font.draw(guiBatch, m.message, 10, 20 + m.posY);
        }

        guiBatch.end();

        //Update player position
        //updatePlayer();
        player.update();
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
        networkManager.update(player.getPos());
	}

    private void cameraBoundsCheck() {
        if(camera.position.x < camera.viewportWidth / 2) camera.position.x = camera.viewportWidth / 2;
        if(camera.position.y < camera.viewportHeight / 2) camera.position.y = camera.viewportHeight / 2;
        if(camera.position.x > mapWidth - camera.viewportWidth / 2) camera.position.x = mapWidth - camera.viewportWidth / 2;
        if(camera.position.y > mapHeight - camera.viewportHeight / 2) camera.position.y = mapHeight - camera.viewportHeight / 2;
    }
}
