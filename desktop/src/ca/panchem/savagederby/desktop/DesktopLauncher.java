package ca.panchem.savagederby.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ca.panchem.savagederby.SavageDerby;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.vSyncEnabled = false;
        config.width = 1280;
        config.height = 720;
        config.foregroundFPS = 144;
        config.backgroundFPS = 60;
		new LwjglApplication(new SavageDerby(), config);
	}
}