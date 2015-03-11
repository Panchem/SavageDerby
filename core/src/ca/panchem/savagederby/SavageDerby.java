package ca.panchem.savagederby;

import ca.panchem.savagederby.screens.MainMenu;

public class SavageDerby extends com.badlogic.gdx.Game {
    @Override
    public void create() {
        setScreen(new MainMenu(this));
    }
}
