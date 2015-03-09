package server;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import server.Network.Packet_Kick;
import server.packets.MPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class SDServer {
    Server server;
    ArrayList<MPlayer> players;
    ConsoleHandler ch = new ConsoleHandler();
    StaticDataHandler staticData;
    String serverPrefix = "Server";
    private int network_delay = 20;

    private void handleInput(String line) {

        String[] commands = line.split(" ");

        if(commands[0].equals("getplayers")) {
            if(players.size() > 0) {
                for (MPlayer p : players) {
                    ch.print(staticData.getName(p.getId()) + " : " + p.getId());
                }
            } else {
                ch.print("No players connected");
            }
        } else if(commands[0].equals("ping")) {
            ch.print("Pong");
        } else if(commands[0].equals("kick")) {
            kick(commands);
        } else if (commands[0].equals("setnetworkdelay")) {
            network_delay = Integer.parseInt(commands[1]);
            broadcast("network delay: " + (1000/network_delay) + " updates/s", serverPrefix);
        } else if (commands[0].equals("broadcast")) {
            String bString = "";
            for (int i = 1; i < commands.length; i++) {
                bString = (bString + commands[i] + " ");
            }
            broadcast(bString, serverPrefix);
        }
        else {
            ch.print("Unrecognized command");
        }
    }

    private void broadcast(String bString, String origin) {
        Network.Packet_Broadcast broadcast = new Network.Packet_Broadcast();
        broadcast.message = (origin + ": " + bString);
        server.sendToAllTCP(broadcast);
    }

    private void kick(String[] commands) {
        players.remove(getMPlayer(Integer.parseInt(commands[1])));

        Packet_Kick p = new Packet_Kick();
        try {
            p.reason = commands[2];
        } catch (ArrayIndexOutOfBoundsException e) {
            p.reason = "generic reasons.";
        }
        p.id = Integer.parseInt(commands[1]);

        server.sendToAllTCP(p);
    }

    public SDServer() throws IOException {

        server = new Server();
        players = new ArrayList<MPlayer>();
        staticData = new StaticDataHandler();

        Network.register(server);

        server.addListener(new Listener() {
            public void received(Connection c, Object o) {
                if (o instanceof Network.Packet_Login) {

                    Network.Packet_Login packet = (Network.Packet_Login) o;
                    ch.print("Join Requested by " + packet.name);

                    MPlayer player = new MPlayer();
                    player.setId(c.getID());
                    player.setPos(new Vector2(350, 350));

                    ch.print("Spawning player " + c.getID() + ", " + packet.name + " at " + player.getPos().x + ", " + player.getPos().y);

                    players.add(player);
                    staticData.addPlayer(c.getID(), packet.name, "playerOne");

                    Network.Packet_Login_Accepted response = new Network.Packet_Login_Accepted();
                    response.id = player.getId();
                    response.spawnPoint = player.getPos();

                    Network.Packet_Static_Pack static_pack = new Network.Packet_Static_Pack();
                    static_pack.staticDataPack = staticData.data;

                    c.sendTCP(response);
                    server.sendToAllTCP(static_pack);
                    broadcast(packet.name + " has joined the game.", serverPrefix);

                } else if(o instanceof Network.Packet_Update_X) {
                    players.get(getMPlayer(((Network.Packet_Update_X) o).id)).setX(((Network.Packet_Update_X) o).x);
                } else if(o instanceof Network.Packet_Update_Y) {
                    players.get(getMPlayer(((Network.Packet_Update_Y) o).id)).setY(((Network.Packet_Update_Y) o).y);
                } else if(o instanceof Network.Packet_Ping) {
                    c.sendTCP(new Network.Packet_Ping());
                }
            }

            public void disconnected (Connection c) {
                try {
                    ch.print(staticData.getName(c.getID()) + " Disconnected");
                    broadcast(staticData.getName(c.getID()) + " has disconnected.", serverPrefix);
                    players.remove(getMPlayer(c.getID()));
                    staticData.data.remove(c.getID());
                    Network.Packet_Static_Pack dp = new Network.Packet_Static_Pack();
                    dp.staticDataPack = staticData.data;
                    server.sendToAllTCP(dp);
                } catch (ArrayIndexOutOfBoundsException e) {
                    ch.print("Somebody dun got kicked");
                    broadcast("Somebody dun got kicked", serverPrefix);
                }
            }
        });

        server.bind(Network.port);
        ch.print("Server bound to port " + Network.port);

        server.start();
        ch.print("Server Started :D");

        final Thread inputThread = new Thread() {

            @Override
            public void run() {
                Scanner sc = new Scanner(System.in);
                String line;

                line = sc.nextLine();
                handleInput(line);
                ch.blank();

                while (!line.equals("stop")) {
                    line = sc.nextLine();
                    handleInput(line);
                    ch.blank();
                    if(line.equals("stop")) System.exit(0);
                }

                System.exit(0);
            }
        };

        final Thread playerUpdateThread = new Thread() {

            @Override
            public void run() {
                Network.Packet_Player_List playerList = new Network.Packet_Player_List();

                while (true) {
                    try {sleep(network_delay);} catch (InterruptedException e) {e.printStackTrace();}

                    playerList.players = new MPlayer[players.size()];

                    for (int i = 0; i < players.size(); i++) {
                        playerList.players[i] = players.get(i);
                    }

                    server.sendToAllTCP(playerList);
                }
            }
        };

        inputThread.start();
        playerUpdateThread.start();
    }

    private int getMPlayer(int id) {
        for (int i = 0; i < players.size(); i++) {
            if(players.get(i).getId() == id) return i;
        }
        return -1;
    }

    public static void main(String[] args) throws IOException {
        Log.set(Log.LEVEL_DEBUG);
        new SDServer();
    }
}
