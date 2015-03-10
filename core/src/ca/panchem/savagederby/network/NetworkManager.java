package ca.panchem.savagederby.network;

import ca.panchem.savagederby.SavageDerby;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import server.Network;
import server.StaticDataHandler;
import server.packets.MPlayer;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;

public class NetworkManager {

    private ArrayList<MPlayer> players;
    private float networkX;
    private float networkY;
    public int id;
    public String name;
    int updateIndexer = 0;
    Client client;
    private boolean connected;
    private boolean busy;
    public StaticDataHandler staticData;
    private long lastPing;
    private int ping;

    public NetworkManager(Vector2 playerPos) {
        players = new ArrayList<>();
        staticData = new StaticDataHandler();
        client = new Client();
        client.start();
        Network.register(client);

        client.addListener(new Listener() {

            public void connected(Connection c) {
            }

            public void disconnected(Connection c) {
                connected = false;
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

                    players.clear();
                    for (Network.Packet_Player_Update p : packet.players) {
                        if(p.id != id) {
                            players.add(Network.expandPlayer(p));
                        }
                    }

                } else if (o instanceof Network.Packet_Kick) {
                    if (((Network.Packet_Kick) o).id == id) {
                        client.close();
                        JOptionPane.showMessageDialog(null, "You have been kicked: " + ((Network.Packet_Kick) o).reason);
                    }
                } else if(o instanceof Network.Packet_Broadcast) {
                    SavageDerby.chatBox.addMessage(((Network.Packet_Broadcast) o).message);
                } else if(o instanceof Network.Packet_Static_Data) {
                    if(((Network.Packet_Static_Data) o).id != id) {
                        staticData.addPlayer(((Network.Packet_Static_Data) o).id, ((Network.Packet_Static_Data) o).name, ((Network.Packet_Static_Data) o).playerType);
                    }
                } else if(o instanceof Network.Packet_Static_Pack) {
                    staticData.setData(((Network.Packet_Static_Pack) o).staticDataPack);
                } else if (o instanceof Network.Packet_Ping) {
                    ping = (int) (System.currentTimeMillis() - lastPing);
                }
            }
        });

        networkX = (int) playerPos.x;
        networkY = (int) playerPos.y;
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

    public void update(MPlayer player) {
        if(updateIndexer % 2 == 0 && connected) {

            //Send movement packets
            if((int) player.getPos().x != networkX) {
                networkX = player.getPos().x;
                Network.Packet_Update_X packet = new Network.Packet_Update_X();
                packet.id = id;
                packet.x = player.getPos().x;
                client.sendTCP(packet);
            }

            if((int) player.getPos().y != networkY) {
                networkY = player.getPos().y;
                Network.Packet_Update_Y packet = new Network.Packet_Update_Y();
                packet.id = id;
                packet.y = player.getPos().y;
                client.sendTCP(packet);
            }

            //Send animation data
            Network.Packet_Anim packet_anim = new Network.Packet_Anim();
            packet_anim.id = id;
            packet_anim.crouching = player.isCrouching();
            packet_anim.direction = player.getBDirection();
            packet_anim.jumping = player.isJumping();
            packet_anim.walking = player.isWalking();
            client.sendTCP(packet_anim);

        } else if(updateIndexer == 225) {
            lastPing = System.currentTimeMillis();
            client.sendTCP(new Network.Packet_Ping());
            updateIndexer = 0;
        }
        updateIndexer++;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    public ArrayList<MPlayer> getPlayers() {
        return players;
    }

    public boolean isConnected() {
        return connected;
    }

    public String getSPing() {
        if(connected) return String.valueOf(ping);
        else return "Not Connected";
    }

    public int getPing() {
        return ping;
    }
}
