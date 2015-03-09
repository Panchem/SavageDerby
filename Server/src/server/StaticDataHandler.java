package server;

import java.util.HashMap;

public class StaticDataHandler {

    HashMap<Integer, Network.StaticData> data;

    public void setData(HashMap<Integer, Network.StaticData> data) {
        this.data = data;
    }

    public StaticDataHandler() {
        data = new HashMap<Integer, Network.StaticData>();
    }

    public String getName(int id) {
        if(data.get(id) == null) return "billy bob joe";
        return data.get(id).name;
    }

    public String getType(int id) {
        return "type";
        //return data.get(id).playerType;
    }

    public void addPlayer(int id, String name, String playerType) {
        Network.StaticData newPlayer = new Network.StaticData();
        newPlayer.name = name;
        newPlayer.playerType = playerType;
        data.put(id ,newPlayer);
    }

    public void addPlayer(Object o) {
        Network.Packet_Static_Data playerData = (Network.Packet_Static_Data) o;
        this.addPlayer(playerData.id, playerData.name, playerData.playerType);
    }
}
