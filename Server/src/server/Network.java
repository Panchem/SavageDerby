package server;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import server.packets.MPlayer;

import java.util.ArrayList;
import java.util.HashMap;

public class Network {

    public static final int port = 25776;

    static public void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();

        kryo.register(Packet_Login.class);
        kryo.register(Packet_Login_Accepted.class);
        kryo.register(Vector2.class);
        kryo.register(Packet_Player_List.class);
        kryo.register(ArrayList.class);
        kryo.register(MPlayer.class);
        kryo.register(Packet_Kick.class);
        kryo.register(Packet_Update_X.class);
        kryo.register(Packet_Update_Y.class);
        kryo.register(Packet_Broadcast.class);
        kryo.register(Packet_Static_Data.class);
        kryo.register(Packet_Static_Pack.class);
        kryo.register(Packet_Player_Update.class);
        kryo.register(Packet_Player_Update[].class);
        kryo.register(Packet_Anim.class);
        kryo.register(Packet_Ping.class);
        kryo.register(StaticData.class);
        kryo.register(HashMap.class);
    }

    public static class Packet_Login {
        public String name;
    }
    public static class Packet_Login_Accepted {
        public Vector2 spawnPoint;
        public int id;
    }
    public static class Packet_Player_List {
        public Packet_Player_Update[] players;
    }
    public static class Packet_Kick {
        public String reason;
        public int id;
    }
    public static class Packet_Update_X {
        public float x;
        public int id;
    }
    public static class Packet_Update_Y {
        public float y;
        public int id;
    }
    public static class Packet_Broadcast {
        public String message;
    }
    public static class Packet_Static_Data {
        public int id;
        public String name;
        public String playerType;
    }
    public static class Packet_Static_Pack {
        public HashMap<Integer, StaticData> staticDataPack;
    }
    public static class StaticData {
        public String name;
        public String playerType;

    }
    public static class Packet_Ping {
    }
    public static class Packet_Player_Update {
        public boolean walking;
        public boolean crouching;
        public boolean jumping;
        public boolean direction;
        public float x;
        public float y;
        public int id;
    }
    
    public static Packet_Player_Update slimPlayer(MPlayer player) throws NullPointerException{

        Packet_Player_Update u = new Packet_Player_Update();

        u.id = player.getId();
        try {
            u.x = player.getPos().x;
            u.y =  player.getPos().y;
            u.walking = player.isWalking();
            u.jumping = player.isJumping();
            u.crouching = player.isCrouching();
            u.direction = player.getBDirection();
        } catch (NullPointerException e) {
            System.out.println("Player Loading...");
        }
        return u;
    }

    public static MPlayer expandPlayer(Packet_Player_Update u) {
        MPlayer exp = new MPlayer();

        exp.setId(u.id);
        exp.setX(u.x);
        exp.setY(u.y);
        exp.setWalking(u.walking);
        exp.setJumping(u.jumping);
        exp.setCrouching(u.crouching);
        exp.setDirection(u.direction);

        return exp;
    }

    public static class Packet_Anim {
        public int id;
        public boolean direction, jumping, walking, crouching;
    }
}
