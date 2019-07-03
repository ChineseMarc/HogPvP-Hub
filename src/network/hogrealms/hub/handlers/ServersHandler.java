package network.hogrealms.hub.handlers;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import network.hogrealms.hub.Hub;
import network.hogrealms.hub.utils.NetworkServer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;

public class ServersHandler implements PluginMessageListener {
  private Hub plugin;
  private HashMap<String, NetworkServer> networkServers = new HashMap<>();

  public ServersHandler(Hub instance) {
    plugin = instance;
    Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
    Bukkit.getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
    for (String s : plugin.getConfig().getConfigurationSection("Servers").getKeys(false)) {
      this.networkServers.put(s, new NetworkServer(s));
    }
    this.networkServers.put("ALL", new NetworkServer("BungeeCord"));
    registerServers();
    NetworkServersOnlineChecker();
  }

  public void onPluginMessageReceived(String channel, Player player, byte[] message) {
    if (channel.equals("BungeeCord")) {
      ByteArrayDataInput input = ByteStreams.newDataInput(message);
      String subChannel = input.readUTF();
      if (subChannel.equals("PlayerCount")) {
        String serverName = input.readUTF();
        int playerCount = input.readInt();
        if (networkServers.containsKey(serverName)) {
          networkServers.get(serverName).setPlayerCount(playerCount);
        }
      }
    }
  }

  private void registerServers() {
    networkServers.forEach(
        (serverName, value) -> {
          if (serverName.equalsIgnoreCase("all")) return;
          String serverIP = plugin.getConfig().getString("Servers." + serverName + "." + "ip");
          int serverPort = plugin.getConfig().getInt("Servers." + serverName + "." + "Port");
          String list = plugin.getConfig().getString("Servers." + serverName + "." + "gui.name");
          value.setServerPort(serverPort);
          value.setServerIP(serverIP);
          value.setGUIName(list);
          sendToBungeeCord(serverName);
        });
  }

  private void NetworkServersOnlineChecker() {
    int sec = 60;
    new BukkitRunnable() {
      public void run() {
        networkServers.forEach(
            (key, networkServer) -> {
              if (key.equalsIgnoreCase("all")) return;
              int serverPort = networkServer.getServerPort();
              String serverIP = networkServer.getServerIP();
              try {
                Socket s = new Socket();
                s.connect(new InetSocketAddress(serverIP, serverPort), 20);
                s.close();
                networkServer.setOnline(true);
              } catch (Exception e) {
                networkServer.setOnline(false);
              }
            });
      }
    }.runTaskTimer(plugin, 20L * sec, 20L * sec);
  }

  private void sendToBungeeCord(String sub) {
    String channel = "PlayerCount";
    ByteArrayOutputStream b = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(b);
    try {
      out.writeUTF(channel);
      out.writeUTF(sub);
    } catch (IOException e) {
      e.printStackTrace();
    }
    Bukkit.getServer().sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
  }

  public boolean isNetworkServerOnline(String serverName) {
    return networkServers.containsKey(serverName) && networkServers.get(serverName).isOnline();
  }

  public int networkServerPlayerCount(String serverName) {
    return networkServers.containsKey(serverName)
        ? networkServers.get(serverName).getPlayerCount()
        : 0;
  }

  public HashMap<String, NetworkServer> getNetworkServers() {
    return networkServers;
  }
}
