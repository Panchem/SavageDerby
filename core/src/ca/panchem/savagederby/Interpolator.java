package ca.panchem.savagederby;

import com.badlogic.gdx.math.Vector2;
import server.packets.MPlayer;

import java.util.ArrayList;

public class Interpolator {
    private ArrayList<MPlayer> lastPosPlayers;
    private ArrayList<MPlayer> currentPosPlayers;
    private ArrayList<Vector2> velocities;
    private ArrayList<MPlayer> interpolatedPlayers;
    private long lastUpdate;
    private float delay;

    public Interpolator(ArrayList<MPlayer> playerpos) {
        interpolatedPlayers = new ArrayList<MPlayer>();
        lastPosPlayers = playerpos;
        velocities = new ArrayList<Vector2>();
        currentPosPlayers = playerpos;
    }

    public ArrayList<MPlayer> update() {
        for (int i = 0; i < velocities.size(); i++) {
            interpolatedPlayers.get(i).getPos().x += velocities.get(i).x / delay;
            interpolatedPlayers.get(i).getPos().y += velocities.get(i).y / delay;
        }

        return interpolatedPlayers;
    }

    private Vector2 differences(Vector2 current, Vector2 last) {
        return new Vector2(current.x - last.x, current.y - last.y);
    }

    public void updatePlayerPositions(ArrayList<MPlayer> players) {
        this.lastPosPlayers = currentPosPlayers;
        this.currentPosPlayers = players;
        delay = System.currentTimeMillis() - lastUpdate;
        lastUpdate = System.currentTimeMillis();

        velocities.clear();
        interpolatedPlayers.clear();
        for (int i = 0; i < lastPosPlayers.size(); i++) {
            velocities.add(i, differences(currentPosPlayers.get(i).getPos(), lastPosPlayers.get(i).getPos()));
            interpolatedPlayers.add(currentPosPlayers.get(i));
        }
    }

    public ArrayList<MPlayer> getInterpolatedPlayers() {
        return interpolatedPlayers;
    }
}
