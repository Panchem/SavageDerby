package ca.panchem.savagederby;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
    Box2DMapObjectParser parser;
    World world;
    Box2DDebugRenderer b2dr;
    Texture otherPlayers;
    NetworkManager networkManager;

    float charXVelocity = 0;
    float charYVelocity = 0;
    float gravityStrength = 12f;
    public static final int mapWidth = 2800;
    public static final int mapHeight = 2100;

    //Client & Networking
    Player player;
    private Body playerBody;
    private boolean grounded;
    public static ChatBox chatBox;

    @Override
	public void create () {
        parser = new Box2DMapObjectParser();
        world = new World(new Vector2(0f, 0f), false);
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture fa = contact.getFixtureA();
                Fixture fb = contact.getFixtureB();

                if(fb.getUserData() != null && fb.getUserData().equals("groundSensor")) {
                    grounded = true;
                } else if(fa.getUserData() != null && fa.getUserData().equals("groundSensor")) {
                    grounded = true;
                }
            }

            @Override
            public void endContact(Contact contact) {
                Fixture fa = contact.getFixtureA();
                Fixture fb = contact.getFixtureB();

                if(fb.getUserData() != null && fb.getUserData().equals("groundSensor")) {
                    grounded = false;
                } else if(fa.getUserData() != null && fa.getUserData().equals("groundSensor")) {
                    grounded = false;
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
        player = new Player(66, 92);
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
        otherPlayers = new Texture(Gdx.files.internal("assets/p2_stand.png"));
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
        b2dr.render(world, camera.combined);

        //Begin sprite drawing
		spriteBatch.begin();

        networkManager.setBusy(true);
        for (MPlayer p : networkManager.getPlayers()) {
            spriteBatch.draw(otherPlayers, p.getPos().x - player.getCharWidth() / 2, p.getPos().y - player.getCharHeight() / 2);
            font.draw(spriteBatch, p.getName(), p.getPos().x - p.getName().length() * font.getSpaceWidth() / 2, p.getPos().y + player.getCharHeight() / 2 + font.getLineHeight());
        }
        networkManager.setBusy(false);

        spriteBatch.draw(player.getSprite(), player.getPos().x - player.getCharWidth() / 2, player.getPos().y - player.getCharHeight() / 2);
        font.draw(spriteBatch, player.getName(), player.getPos().x - player.getName().length() * font.getSpaceWidth() / 2, player.getPos().y + player.getCharHeight() / 2 + font.getLineHeight());

        spriteBatch.end();

        chatBox.update();
        guiBatch.begin();
        font.draw(guiBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 720 - 10);
        font.draw(guiBatch, "Grounded: " + grounded, 10, 720 - 30);
        font.draw(guiBatch, "Gravity: " + charYVelocity, 10, 720 - 50);

        for (Message m : chatBox.getMessages()) {
            font.draw(guiBatch, m.message, 10, 20 + m.posY);
        }

        guiBatch.end();

        //Update player position
        updatePlayer();

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

    private void updatePlayer() {
        float moveSpeed = 800;
        float friction = 0.8f;
        float airResistance = 0.99f;
        float airMove = 0.4f;

        if(!grounded) {
            charYVelocity -= gravityStrength;
            charXVelocity *= airResistance;
        } else {
            charYVelocity = 0;
            charXVelocity *= friction;
        }

        player.getPos().y += charYVelocity * Gdx.graphics.getDeltaTime();
        player.getPos().x += charXVelocity * Gdx.graphics.getDeltaTime();

        if(Gdx.input.isKeyPressed(Input.Keys.SPACE) && grounded)  {
            grounded = false;
            charYVelocity = 1000;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.A) && grounded) {
            //player.getPos().x -= moveSpeed * Gdx.graphics.getDeltaTime();
            charXVelocity = -moveSpeed;
        } else if(Gdx.input.isKeyPressed(Input.Keys.A)){
            charXVelocity = -moveSpeed * airMove;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D) && grounded) {
            //player.getPos().x += moveSpeed * Gdx.graphics.getDeltaTime();
            charXVelocity = moveSpeed;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            charXVelocity = moveSpeed * airMove;
        }

        playerBody.setTransform(player.getPos(), 0);
    }
}
