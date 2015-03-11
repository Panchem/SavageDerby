package ca.panchem.savagederby.screens;

import ca.panchem.savagederby.utils.ResourceLoader;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class MainMenu extends SDScreen {

    BitmapFont font;
    SpriteBatch batch;
    ResourceLoader res;
    Stage stage;
    Texture playerIcon;
    int playerSelection;

    public MainMenu(Game game) {
        super(game);
    }

    public void show() {
        font = new BitmapFont();
        batch = new SpriteBatch();
        stage = new Stage();
        res = new ResourceLoader("assets/");
        playerSelection = 1;

        //Load resources
        res.load("steve", "hud/hud_p1.png");
        res.load("gary", "hud/hud_p2.png");
        res.load("p3icon", "hud/hud_p3.png");

        playerIcon = res.getSprite("p1icon");
    }

    public void render(float delta) {

        //Clear the screen with a black background
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        String selection;

        if(playerSelection == 0) {
            playerIcon = res.getSprite("gary");
            selection = "Gary";
        } else if (playerSelection == 1) {
            playerIcon = res.getSprite("steve");
            selection = "Steve";
        } else if (playerSelection == 2) {
            playerIcon = res.getSprite("p3icon");
            selection = "Billy";
        } else {
            selection = "null";
        }

        batch.begin();
        font.draw(batch, "Press O for online server, L for local server, I for internal server",50, 720/2);
        font.draw(batch, "Buttons 1-3 for Character Selection",50, 720/2 - 30);
        font.draw(batch, "Currently selected player: " + selection, 50, 720/2 - 60);
        batch.draw(playerIcon, 50, 720/2 - 150);

        //Draw Character Selection
        batch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.O)) {
            game.setScreen(new GameClass(game, "Default Name", "cheesemc.com", playerSelection, false));
        } else if (Gdx.input.isKeyPressed(Input.Keys.L)) {
            game.setScreen(new GameClass(game, "Default Name", "localhost", playerSelection, false));
        } else if (Gdx.input.isKeyPressed(Input.Keys.I)) {
            game.setScreen(new GameClass(game, "Default Name", playerSelection));
        } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
            playerSelection = 0;
        } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
            playerSelection = 1;
        } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
            playerSelection = 2;
        }
    }
}
