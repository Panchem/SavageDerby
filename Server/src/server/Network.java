package server;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import server.packets.MPlayer;

import java.util.ArrayList;

public class Network {

    public static final int port = 25776;

    static public void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();

        kryo.register(Packet_Login.class);
        kryo.register(Packet_Login_Accepted.class);
        kryo.register(PacketPlayerUpdate.class);
        kryo.register(Vector2.class);
        kryo.register(Packet_Player_List.class);
        kryo.register(ArrayList.class);
        kryo.register(MPlayer.class);
        kryo.register(Packet_Kick.class);
        kryo.register(Packet_Update_X.class);
        kryo.register(Packet_Update_Y.class);
        kryo.register(Packet_Broadcast.class);
    }

    public static class Packet_Login {
        public String name;
    }
    public static class Packet_Login_Accepted {
        public Vector2 spawnPoint;
        public int id;
    }
    public static class Packet_Player_List {
        public ArrayList<MPlayer> players = new ArrayList<MPlayer>();
    }
    public static class Packet_Kick {
        public String reason;
        public int id;
    }
    public static class Packet_Update_X {
        public int x;
        public int id;
    }
    public static class Packet_Update_Y {
        public int y;
        public int id;
    }
    public static class PacketPlayerUpdate {
        public Vector2 pos;
        public int id;
    }
    public static class Packet_Broadcast {
        public String message;
    }
}
