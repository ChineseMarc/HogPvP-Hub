package network.hogrealms.hub.queue;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import network.hogrealms.hub.Hub;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

import static com.google.common.io.ByteStreams.newDataOutput;


public class Queue {
    private String server;
    private List<Player> players;
    private Map<Player, BukkitTask> taskMap;
    private boolean paused;
    private int limit;
    private Hub plugin;
    private QueueManager queueManager;

    public Queue(String server, Hub instance, QueueManager queueManager) {
        plugin = instance;
        this.server = server;
        this.queueManager = queueManager;
        players = new ArrayList<>();
        taskMap = new HashMap<>();
        paused = false;
        limit = 500;
        new BukkitRunnable() {
            public void run() {
                for (Player o : players)
                    if (o.isOnline()) {
                        o.sendMessage("§7§m-*----------------------------------*-");
                        o.sendMessage("§dIf you want to skip the queue, purchase");
                        o.sendMessage("§da rank at buycraft.§7(Soon)");
                        o.sendMessage("§7§m-*----------------------------------*-");
                    } else {
                        players.remove(o);
                    }
            }
        }.runTaskTimer(plugin, 10L, 10L);
    }

    private Player getFirstPlayer() {
        return (Player) Iterables.getFirst(ImmutableList.of(Bukkit.getServer().getOnlinePlayers()), null);
    }

    public void clear() {
        players.clear();
        taskMap.clear();
    }

    public void addEntry(Player p) {
        if (players.contains(p)) {
            return;
        }
        if (queueManager.getPriority(p) == 0) {
            p.sendMessage("§dYou bypassed queue for §f" + server + "§b.");
            connectServer(p.getName(), server);
            return;
        }
    p.sendMessage("§dYou have been added to queue for §f" + server + "§b.");
        players.add(p);
        for (Player u : players) {
            int pos = players.indexOf(u);
            if ((u != p) && (queueManager.getPriority(p) < queueManager.getPriority(u))) {
                if (players.get(pos).isOnline()) {
                    players.get(pos).sendMessage("§dSomeone with higher queue priority has joined the queue!");
                }
                Collections.swap(players, pos, players.size() - 1);
            }
        }
    }

    public void removeEntry(Player p) {
        if (!players.contains(p)) {
            return;
        }
        players.remove(p);
    }

    public int getSize() {
        return players.size();
    }

    public Player getPlayerAt(int p) {
        return players.get(p);
    }

    void sendFirst() {
        if (!players.isEmpty()) {
            Player p = players.get(0);
            ByteArrayDataOutput out = newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(server);
            p.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
        }
    }

    public void sendDirect(Player p) {
        ByteArrayDataOutput out = newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        p.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    private void connectServer(String playerName, String server) {
        ByteArrayDataOutput out = newDataOutput();
        out.writeUTF("ConnectOther");
        out.writeUTF(playerName);
        out.writeUTF(server);
        sendBungeeMessage(out);
    }

    private void sendBungeeMessage(ByteArrayDataOutput message) {
        sendBungeeMessage(getFirstPlayer(), message);
    }

    private void sendBungeeMessage(Player player, ByteArrayDataOutput message) {
        player.sendPluginMessage(plugin, "BungeeCord", message.toByteArray());
    }

    public String getServer() {
        return server;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Map<Player, BukkitTask> getTaskMap() {
        return taskMap;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean p) {
        paused = p;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int i) {
        limit = i;
    }
}