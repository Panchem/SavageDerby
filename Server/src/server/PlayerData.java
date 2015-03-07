package server;

import server.packets.MPlayer;

public class PlayerData {

    private final int id;
    private MPlayer player;
    private long lastUpdate = 0;

    public PlayerData(MPlayer player) {
        this.player = player;
        this.id = player.getId();
    }

    public MPlayer getPlayer() {
        return player;
    }

    public long getLastUpdateTime() {
        return System.currentTimeMillis() - lastUpdate;
    }

    public int getId() {
        return id;
    }

    public void update() {
        this.lastUpdate = System.currentTimeMillis();
    }
}
