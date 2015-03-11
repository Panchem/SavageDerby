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
import java.util.HashMap;
import java.util.Scanner;

public class SDServer {
    Server server;
    ArrayList<MPlayer> players;
    ConsoleHandler ch = new ConsoleHandler();
    StaticDataHandler staticData;
    private int network_delay = 20;
    Commands commandHandler;
    boolean running = true;
    String[] commands;

    public SDServer() throws IOException {

        server = new Server();
        players = new ArrayList<>();
        staticData = new StaticDataHandler();
        commandHandler = new Commands();
        Network.register(server);

        server.addListener(new Listener() {
            public void received(Connection c, Object o) {
                if (o instanceof Network.Packet_Login) {

                    Network.Packet_Login packet = (Network.Packet_Login) o;
                    ch.print("Join Requested by " + packet.name);

                    MPlayer player = new MPlayer();
                    player.setId(c.getID());
                    player.setPos(new Vector2(1500, 850));

                    ch.print("Spawning player " + c.getID() + ", " + packet.name + " at " + player.getPos().x + ", " + player.getPos().y);

                    players.add(player);
                    staticData.addPlayer(c.getID(), packet.name, packet.type);

                    Network.Packet_Login_Accepted response = new Network.Packet_Login_Accepted();
                    response.id = player.getId();
                    response.spawnPoint = player.getPos();

                    Network.Packet_Static_Pack static_pack = new Network.Packet_Static_Pack();
                    static_pack.staticDataPack = staticData.data;

                    c.sendTCP(response);
                    server.sendToAllTCP(static_pack);
                    commandHandler.broadcast(packet.name + " has joined the game.");

                } else if(o instanceof Network.Packet_Update_X) {
                    players.get(getMPlayer(((Network.Packet_Update_X) o).id)).setX(((Network.Packet_Update_X) o).x);
                } else if(o instanceof Network.Packet_Update_Y) {
                    players.get(getMPlayer(((Network.Packet_Update_Y) o).id)).setY(((Network.Packet_Update_Y) o).y);
                } else if(o instanceof Network.Packet_Ping) {
                    c.sendTCP(new Network.Packet_Ping());
                } else if (o instanceof Network.Packet_Anim) {
                    Network.Packet_Anim packet = (Network.Packet_Anim) o;
                    int playerID = getMPlayer(packet.id);

                    players.get(playerID).setDirection(packet.direction);
                    players.get(playerID).setCrouching(packet.crouching);
                    players.get(playerID).setWalking(packet.walking);
                    players.get(playerID).setJumping(packet.jumping);
                }
            }

            public void disconnected (Connection c) {
                try {
                    ch.print(staticData.getName(c.getID()) + " Disconnected");
                    commandHandler.broadcast(staticData.getName(c.getID()) + " has disconnected.");
                    players.remove(getMPlayer(c.getID()));
                    staticData.data.remove(c.getID());
                    Network.Packet_Static_Pack dp = new Network.Packet_Static_Pack();
                    dp.staticDataPack = staticData.data;
                    server.sendToAllTCP(dp);
                } catch (ArrayIndexOutOfBoundsException e) {
                    ch.print("Somebody dun got kicked");
                    commandHandler.broadcast("Somebody dun got kicked");
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

                while (running) {
                    try {sleep(network_delay);} catch (InterruptedException e) {e.printStackTrace();}

                    playerList.players = new Network.Packet_Player_Update[players.size()];

                    for (int i = 0; i < players.size(); i++) {
                        playerList.players[i] = Network.slimPlayer(players.get(i));
                    }

                    if(playerList.players.length >= 1)server.sendToAllTCP(playerList);
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

    private void handleInput(String line) {
        commands = line.split(" ");

        switch (commands[0]) {
            case "players"      : commandHandler.printPlayers();
                                    break;
            case "kick"         : commandHandler.kick();
                                    break;
            case "networkdelay" : commandHandler.setNetworkDelay();
                                    break;
            case "broadcast"    : commandHandler.broadcast();
                                    break;
            case "help"         : commandHandler.printHelp();
                                    break;
            case "stop"         : running = false;
                                  System.exit(0);
                                  break;
            default             : commandHandler.unknownCommand();
        }
    }

    private class Commands {
        HashMap<String, String> commandList;

        public Commands() {
            commandList = new HashMap<>();
            loadHelp();
        }

        public void register(String command, String info) {
            commandList.put(command, info);
        }

        public void printHelp() {
            for (String s : commandList.keySet()) {
                ch.print(s + ": " + commandList.get(s));
            }
        }

        public void loadHelp() {
            register("players", "Returns a list of player names with their ID numbers");
            register("kick", "Kicks a player from the server, takes <ID Number> and <Message>");
            register("networkdelay", "Sets the server update interval");
            register("broadcast", "Sends a message to all players");
            register("help", "display this");
        }

        public void printPlayers() {
            if(players.size() > 0) {
                for (MPlayer p : players) {
                    ch.print(staticData.getName(p.getId()) + " : " + p.getId() + " <> " + MPlayer.PlayerTypes.getTypeString(p.getPlayerType()));
                }
            } else {
                ch.print("No players connected");
            }
        }

        public void kick() {
            players.remove(getMPlayer(Integer.parseInt(commands[1])));
            Packet_Kick p = new Packet_Kick();
            try {
                p.reason = commands[2];
            } catch (ArrayIndexOutOfBoundsException e) {
                p.reason = "generic reasons.";
            }

            try {
                p.id = Integer.parseInt(commands[1]);
            } catch (ArrayIndexOutOfBoundsException e) {
                ch.print("That player is not connected");
            }
            server.sendToAllTCP(p);
        }

        public void setNetworkDelay() {
            try {
                network_delay = Integer.parseInt(commands[1]);
            } catch (NumberFormatException e) {
                ch.print("That was not a number");
            }
            broadcast("network delay: " + (1000/network_delay) + " updates/s");
        }

        public void broadcast() {
            String bString = "";
            for (int i = 1; i < commands.length; i++) {
                bString = (bString + commands[i] + " ");
            }
            Network.Packet_Broadcast broadcast = new Network.Packet_Broadcast();
            broadcast.message = ("[Server]" + ": " + bString);
            server.sendToAllTCP(broadcast);
        }

        public void broadcast(String message) {
            Network.Packet_Broadcast broadcast = new Network.Packet_Broadcast();
            broadcast.message = ("[Server]" + ": " + message);
            server.sendToAllTCP(broadcast);
        }

        public void unknownCommand() {
            ch.print("Unknown Command");
        }
    }
}
