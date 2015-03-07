package ca.panchem.savagederby;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import server.Network;
import server.packets.MPlayer;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;

public class NetworkManager {
    private ArrayList<MPlayer> players;
    //public static Interpolator interpolator;
    private int networkX;
    private int networkY;
    public int id;
    public int name;
    int updateIndexer = 0;
    Client client;
    private boolean connected;
    private boolean busy;

    public NetworkManager(Vector2 playerPos) {
        players = new ArrayList<MPlayer>();
        //interpolator = new Interpolator(players);
        client = new Client();
        client.start();
        Network.register(client);

        client.addListener(new Listener() {
            public void connected(Connection c) {
            }

            public void disconnected(Connection c) {
                System.out.println("Server has closed");
                SavageDerby.chatBox.addMessage("Server has closed");
            }

            public void received(Connection c, Object o) {
                if (o instanceof Network.Packet_Login_Accepted) {
                    Network.Packet_Login_Accepted packet = (Network.Packet_Login_Accepted) o;
                    networkX = (int) packet.spawnPoint.x;
                    networkY = (int) packet.spawnPoint.y;
                    id = packet.id;
                    connected = true;
                    System.out.println("Connection Accepted");
                    SavageDerby.chatBox.addMessage("Connected to server.");
                } else if (o instanceof Network.Packet_Player_List && !busy) {
                    Network.Packet_Player_List packet = (Network.Packet_Player_List) o;

                    //Clear the list of players
                    players.clear();

                    //Add the new players
                    for (MPlayer p : packet.players) {
                        if(p.getId() != id) {
                            players.add(p);
                        }
                    }
                    //interpolator.updatePlayerPositions(packet.players);
                } else if (o instanceof Network.Packet_Kick) {
                    if (((Network.Packet_Kick) o).id == id) {
                        client.close();
                        JOptionPane.showMessageDialog(null, "You have been kicked: " + ((Network.Packet_Kick) o).reason);
                    }
                } else if(o instanceof Network.Packet_Broadcast) {
                    SavageDerby.chatBox.addMessage(((Network.Packet_Broadcast) o).message);
                }
            }
        });

        networkX = (int) playerPos.x;
        networkY = (int) playerPos.y;
    }

    public ArrayList<MPlayer> getPlayers() {
        return players;
    }

    public void connect(String ipAddress) {
        try {
            client.connect(5000, ipAddress, Network.port);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Could not connect to server");
        }
    }

    public void login(String name) {
        Network.Packet_Login loginRequest = new Network.Packet_Login();
        loginRequest.name = name;
        client.sendTCP(loginRequest);
    }

    public void update(Vector2 playerPos) {
        if(updateIndexer == 3 && connected) {
            if((int) playerPos.x != networkX) {
                networkX = (int) playerPos.x;
                Network.Packet_Update_X packet = new Network.Packet_Update_X();
                packet.id = id;
                packet.x = (int) playerPos.x;
                client.sendTCP(packet);
            }
            if((int) playerPos.y != networkY) {
                networkY = (int) playerPos.y;
                Network.Packet_Update_Y packet = new Network.Packet_Update_Y();
                packet.id = id;
                packet.y = (int) playerPos.y;
                client.sendTCP(packet);
            }
            updateIndexer = 0;
        } else if(connected) {
            updateIndexer++;
        }

        //interpolator.update();
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }
}
