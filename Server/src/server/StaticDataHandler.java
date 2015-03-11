package server;

import java.util.HashMap;

public class StaticDataHandler {

    HashMap<Integer, Network.StaticData> data;

    public void setData(HashMap<Integer, Network.StaticData> data) {
        this.data = data;
    }

    public StaticDataHandler() {
        data = new HashMap<>();
    }

    public String getName(int id) {
        if(data.get(id) == null) return "Loading...";
        return data.get(id).name;
    }

    public int getType(int id) {
        try {
            return data.get(id).playerType;
        } catch (NullPointerException e) {
            return 0;
        }
    }

    public void addPlayer(int id, String name, int playerType) {
        Network.StaticData newPlayer = new Network.StaticData();
        newPlayer.name = name;
        newPlayer.playerType = playerType;
        data.put(id ,newPlayer);
    }
}
